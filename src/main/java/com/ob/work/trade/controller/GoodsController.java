package com.ob.work.trade.controller;

import com.ob.common.constant.Constants;
import com.ob.work.trade.dto.GoodsUpdateDto;
import com.ob.work.trade.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Author: oubin
 * @Date: 2019/9/23 11:24
 * @Description:
 */
@RestController
@RequestMapping(value = Constants.VERSION_01 + "/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @RequestMapping(method = RequestMethod.POST)
    public void addGoodsInfo(@Valid @RequestBody GoodsUpdateDto saveDto) {
        goodsService.addGoodsInfo(saveDto);
    }
}