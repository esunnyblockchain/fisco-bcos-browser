package org.bcos.browser.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bcos.browser.base.BcosBrowserException;
import org.bcos.browser.base.ConstantCode;
import org.bcos.browser.base.Constants;
import org.bcos.browser.base.utils.DateTimeUtils;
import org.bcos.browser.dto.TbAddWarrantEventDto;
import org.bcos.browser.dto.TbCnsContractDto;
import org.bcos.browser.dto.TbMarketAuctionSuccessEventDto;
import org.bcos.browser.dto.TbTransactionDto;
import org.bcos.browser.entity.base.BaseRspEntity;
import org.bcos.browser.entity.req.ReqProofEntity;
import org.bcos.browser.entity.rsp.RspTbWarrantInfoVO;
import org.bcos.browser.entity.rsp.RspVerifyResult;
import org.bcos.browser.entity.rsp.RspVerifyResult.RspEvent;
import org.bcos.browser.service.TbAddWarrantEventService;
import org.bcos.browser.service.TbCnsContractService;
import org.bcos.browser.service.TbMarketAuctionSuccessService;
import org.bcos.browser.service.TbTransactionService;
import org.bcos.web3j.abi.EventEncoder;
import org.bcos.web3j.abi.EventValues;
import org.bcos.web3j.abi.FunctionReturnDecoder;
import org.bcos.web3j.abi.TypeReference;
import org.bcos.web3j.abi.datatypes.Event;
import org.bcos.web3j.abi.datatypes.Type;
import org.bcos.web3j.protocol.ObjectMapperFactory;
import org.bcos.web3j.protocol.core.methods.response.AbiDefinition;
import org.bcos.web3j.protocol.core.methods.response.AbiDefinition.NamedType;
import org.bcos.web3j.protocol.core.methods.response.EthBlock.Block;
import org.bcos.web3j.protocol.core.methods.response.Log;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.esunny.BuildSolidityParams;
import com.esunny.EventResult;
import com.esunny.TrieProof;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.util.Pair;

@Controller
@RequestMapping(value = "warrant")
public class WarrantController {

    @Autowired
    TbAddWarrantEventService tbAddWarrantEventService;
    
    @Autowired
    TbMarketAuctionSuccessService tbMarketAuctionSuccessService;
    
    @Autowired
    TbTransactionService tbTransactionService;
    
    @Autowired
    TbCnsContractService tbCnsContractService;

    private static Logger LOGGER =  LoggerFactory.getLogger(WarrantController.class);
    
    @RequestMapping(value = "/warrantList.page", method = RequestMethod.GET)
    public String toHomePage(){
        LOGGER.info("to page:home.....");
        return "warrantList";
    }
    
    @RequestMapping(value = "/warrantVerify.page", method = RequestMethod.GET)
    public String toVerifyPage(){
        LOGGER.info("to page:verify.....");
        return "warrantVerify";
    }


    @ResponseBody
    @RequestMapping(value = "/getVerifyResult.json",method = { RequestMethod.GET, RequestMethod.POST })
    public BaseRspEntity verifyProof(@RequestBody ReqProofEntity proof) { 
        BaseRspEntity entity = new BaseRspEntity();
        try {
            
            TransactionReceipt receipt = proof.receipt;
            Block block = proof.block;
            List<String> paths = proof.proofs;
            
            boolean valid = TrieProof.verify(block.getReceiptsRoot(), paths, receipt.getTransactionIndexRaw(), receipt.getHash());
            RspVerifyResult result = new RspVerifyResult();
            result.valid = valid;

            if (valid) {
                result.blockHash = block.getHash();
                result.blockNumber = block.getNumber().intValue();
                result.timeStr = DateTimeUtils.BigInteger2String(block.getTimestamp(), Constants.DEFAULT_DATA_TIME_FORMAT);
                
                result.transactionIndex = receipt.getTransactionIndex().intValue();
                result.transactionHash = receipt.getTransactionHash();
                
                TbTransactionDto transactionDto = tbTransactionService.getTbTransactionByPkHash(receipt.getTransactionHash());
                result.transactionFrom  = transactionDto.getTransactionFrom();
                result.transactionTo = transactionDto.getTransactionTo();
                
                List<Log> logs = receipt.getLogs();
                for (Log log : logs) {
                    TbCnsContractDto contractDto = tbCnsContractService.getContractAInfoByAddress(log.getAddress());
                    if (contractDto == null)
                        break;
                    List<AbiDefinition> abiDefinitions = loadContractDefinition(contractDto.getAbi());
                    for (AbiDefinition def : abiDefinitions) {
                        if (!def.getType().equals("event"))
                            continue;
                        String methodId = BuildSolidityParams.buildMethodId(def); 
                        if (!log.getTopics().get(0).substring(0, 10).equals(methodId)) 
                            continue;
                        EventResult eventResult = getEvent(log, def);
                        
                        RspEvent rspEvent = new RspEvent();
                        rspEvent.contractAddress = log.getAddress();
                        rspEvent.contractName = contractDto.getContractName(); 
                        rspEvent.eventName = eventResult.getName();
                        for (int i = 0; i < eventResult.getParams().size(); ++i) {
                            rspEvent.eventParams.add(eventResult.getParams().get(i).getName());
                            rspEvent.eventValues.add(eventResult.getValues().get(i).toString());
                        }
                        result.events.add(rspEvent);
                        break;
                    } 
                }
            }
            
            entity.setData(result);
            entity.setStatus(ConstantCode.SUCCESS.getCode());
            entity.setMsg(ConstantCode.SUCCESS.getMsg());
        } catch (Exception e) {
            entity.setStatus(ConstantCode.QUERY_FAIL_PROOF_FORMAT_INVALID.getCode());
            entity.setMsg(ConstantCode.QUERY_FAIL_PROOF_FORMAT_INVALID.getMsg());
        }
        
        
        return entity;
    }
    
    @ResponseBody
    @RequestMapping(value = "/getTbWarrantInfoByPage.json", method = { RequestMethod.GET, RequestMethod.POST })
    public BaseRspEntity getTbWarrantInfoByPage(){
    
        List<TbAddWarrantEventDto> warrantEvents = tbAddWarrantEventService.getAllAddWarrantEvent();
        
        List<RspTbWarrantInfoVO> list = null;
        if (!warrantEvents.isEmpty()) {
            list = new ArrayList<>();
            for (TbAddWarrantEventDto event : warrantEvents) {
                LOGGER.debug("getWarrantDetail:{}", event);
                RspTbWarrantInfoVO rspEntity = new RspTbWarrantInfoVO();
                rspEntity.setWarrantId(Integer.parseInt(event.getWarrantID()));
                String warrantDetailStr = event.getWarrantDetail();

                LOGGER.debug("getWarrantDetail:{}", warrantDetailStr);
                List<String> warrantDetailStrs = JSONObject.parseArray(warrantDetailStr, String.class);
                if (warrantDetailStrs.size() >= 1) {
                    rspEntity.setWarrantName(warrantDetailStrs.get(0));
                }
                if (warrantDetailStrs.size() >= 2) {
                    rspEntity.setWarrantQty(Integer.parseInt(warrantDetailStrs.get(1)));
                }
                if (warrantDetailStrs.size() >= 3) {
                    rspEntity.setWarrantOwner(warrantDetailStrs.get(2));
                }
                rspEntity.setTransactionHash(event.getTransactionHash());
                rspEntity.setBlockNumber(event.getBlockNumber());
                rspEntity.setDateTimeStr((DateTimeUtils.Timestamp2String(event.getBlockTimestamp(), Constants.DEFAULT_DATA_TIME_FORMAT)));
                list.add(rspEntity);
            }
        }
        
        
        /*BasePageRespEntity response = new BasePageRespEntity();
        response.setRetCode(ConstantCode.SUCCESS);
        response.setPageNumber(1);
        response.setPageSize(1);
        response.setTotal(warrantEvents.size());
        response.setList(list);*/
        BaseRspEntity response = new BaseRspEntity();
        response.setData(list);
        response.setStatus(ConstantCode.SUCCESS.getCode());
        response.setMsg(ConstantCode.SUCCESS.getMsg());
 
        LOGGER.info("getWarrantInfo.end response:{}", JSON.toJSONString(response));
        return response;
    }
     
    /**
     *@Description:Open the block details page
     */
    @RequestMapping(value = "/getWarrantDetailPage.page", method = RequestMethod.GET)
    public ModelAndView getWarrantDetailPage(String warrantId){
        LOGGER.info("getWarrantDetail.start tokenId:{}",warrantId);
        
        try {
            Integer.parseInt(warrantId);
        } catch (NumberFormatException e) { 
            throw new BcosBrowserException(ConstantCode.QUERY_FAIL_WARRANT_ID_INVALID);
        } 
        
        TbAddWarrantEventDto transferEvent = tbAddWarrantEventService.getAddWarrantEventByID(warrantId);
        
        if (transferEvent == null) {
            throw new BcosBrowserException(ConstantCode.QUERY_FAIL_WARRANT_ID_INVALID);
        }
        
        List<TbMarketAuctionSuccessEventDto> marketEvents = tbMarketAuctionSuccessService.getMarketAuctionSuccessEventByID(warrantId);
        
          
        ModelAndView mav = new ModelAndView();
        mav.setViewName("warrantDetail");  
        mav.addObject("warrantId", warrantId);
        mav.addObject("transferEvent", transferEvent);
        mav.addObject("marketEvents", marketEvents);
        LOGGER.info("getWarrantDetail page end,response:{}", JSON.toJSONString(mav));
        return mav;
    }
    
    /**
     * 根据Event的ABI，从Log中解析出内容
     * 
     * @param log      日志对象
     * @param eventAbi 事件的ABI
     * @return EventResult 事件内容
     */
    private static EventResult getEvent(Log log, AbiDefinition eventAbi) {
        // constructs events
        List<Pair<Integer, Integer>> paramsIndex = new ArrayList<Pair<Integer, Integer>>();
        List<TypeReference<?>> indexedParams = new ArrayList<TypeReference<?>>();
        List<TypeReference<?>> unindexedParams = new ArrayList<TypeReference<?>>();
        List<NamedType> params = eventAbi.getInputs();
        for (int i = 0; i < params.size(); ++i) {
            NamedType param = params.get(i);
            if (param.isIndexed()) {
                paramsIndex.add(new Pair<Integer, Integer>(1, indexedParams.size()));
                indexedParams.add(BuildSolidityParams.getType(param.getType()));
            } else {
                paramsIndex.add(new Pair<Integer, Integer>(0, unindexedParams.size()));
                unindexedParams.add(BuildSolidityParams.getType(param.getType()));
            }
        }
        Event event = new Event(eventAbi.getName(), indexedParams, unindexedParams);

        // parse logs
        EventValues eventValues = extractEventParameters(event, log);
        if (eventValues == null) { // parse failed?
            return null;
        }

        // constructs results
        EventResult eventResult = new EventResult(eventAbi.getName());
        for (int i = 0; i < params.size(); ++i) {
            eventResult.addParam(params.get(i));
            Pair<Integer, Integer> index = paramsIndex.get(i);
            if (index.getKey() > 0) {
                eventResult.addValue(eventValues.getIndexedValues().get(index.getValue()));
            } else {
                eventResult.addValue(eventValues.getNonIndexedValues().get(index.getValue()));
            }
        }
        return eventResult;
    }

    /**
     *
     * @desc 为了能在sol实例化出来的java类里面用到static的方法，而不需要实例化这个java类(load方法，用了name
     *       service，屏蔽了自身address)
     */
    private static EventValues extractEventParameters(Event event, Log log) {

        List<String> topics = log.getTopics();
        String encodedEventSignature = EventEncoder.encode(event);
        if (!topics.get(0).equals(encodedEventSignature)) {
            return null;
        }

        List<Type> indexedValues = new ArrayList<>();
        List<Type> nonIndexedValues = FunctionReturnDecoder.decode(log.getData(), event.getNonIndexedParameters());

        List<TypeReference<Type>> indexedParameters = event.getIndexedParameters();
        for (int i = 0; i < indexedParameters.size(); i++) {
            Type value = FunctionReturnDecoder.decodeIndexedValue(topics.get(i + 1), indexedParameters.get(i));
            indexedValues.add(value);
        }
        return new EventValues(indexedValues, nonIndexedValues);
    }
    
    private static List<AbiDefinition> loadContractDefinition(String abi) // copy....
            throws IOException {
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        AbiDefinition[] abiDefinition = objectMapper.readValue(abi, AbiDefinition[].class);
        return Arrays.asList(abiDefinition);
    }
}
