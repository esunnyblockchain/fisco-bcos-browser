package org.bcos.browser.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.bcos.browser.base.BcosBrowserException;
import org.bcos.browser.base.ConstantCode;
import org.bcos.browser.base.Constants;
import org.bcos.browser.base.utils.DateTimeUtils;
import org.bcos.browser.dto.TbAddWarrantEventDto;
import org.bcos.browser.dto.TbMarketAuctionSuccessEventDto;
import org.bcos.browser.entity.base.BasePageRespEntity;
import org.bcos.browser.entity.base.BaseRspEntity;
import org.bcos.browser.entity.req.ReqGetTbBlockInfoByPageVO;
import org.bcos.browser.entity.rsp.RspTbWarrantInfoVO;
import org.bcos.browser.service.TbAddWarrantEventService;
import org.bcos.browser.service.TbMarketAuctionSuccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping(value = "warrant")
public class WarrantController {

    @Autowired
    TbAddWarrantEventService tbAddWarrantEventService;
    
    @Autowired
    TbMarketAuctionSuccessService tbMarketAuctionSuccessService ;

    private static Logger LOGGER =  LoggerFactory.getLogger(WarrantController.class);
    
    @RequestMapping(value = "/warrantList.page", method = RequestMethod.GET)
    public String toHomePage(){
        LOGGER.info("to page:home.....");
        return "warrantList";
    }
    
    
    @ResponseBody
    @RequestMapping(value = "/getTbWarrantInfoByPage.json",method = RequestMethod.GET)
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
    @RequestMapping(value = "/getWarrantDetailPage.page",method = RequestMethod.GET)
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
}
