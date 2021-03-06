package org.bcos.browser.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.bcos.browser.dto.TbAddWarrantEventDto;
import org.bcos.browser.mapper.TbAddWarrantEventMapper;
import org.bcos.browser.service.TbAddWarrantEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

@Service
public class TbAddWarrantEventServiceImpl implements TbAddWarrantEventService{
    private static Logger LOGGER =  LoggerFactory.getLogger(TbAddWarrantEventServiceImpl.class);

    @Autowired
    TbAddWarrantEventMapper tbAddWarrantEventMapper;

    /**
     *@Description: Get the total number of records in the transaction information table
     */
    @Override
    public List<TbAddWarrantEventDto> getAllAddWarrantEvent() {
        LOGGER.info("getAllAddWarrantEvent start.");
        List<TbAddWarrantEventDto> ret = tbAddWarrantEventMapper.getAllAddWarrantEvent();
        LOGGER.info("getAllAddWarrantEvent end result size:{}", ret.size());
        return ret;
    }

    @Override
    public TbAddWarrantEventDto getAddWarrantEventByID(@Param("warrantID")String warrantID) {
        LOGGER.info("getAddWarrantEventByID start.");
        TbAddWarrantEventDto ret = tbAddWarrantEventMapper.getAddWarrantEventByID(warrantID);
        LOGGER.info("getAddWarrantEventByID end result warrantID:{}", warrantID);
        return ret;
    }
}
