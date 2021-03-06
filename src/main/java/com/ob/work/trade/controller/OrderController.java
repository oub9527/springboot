package com.ob.work.trade.controller;

import com.ob.common.constant.Constants;
import com.ob.work.trade.entity.Order;
import com.ob.work.trade.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: oubin
 * @Date: 2019/9/23 14:08
 * @Description:
 */
@RestController
@RequestMapping(value = Constants.VERSION_01 + "/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "/create/goods/{goods_id}", method = RequestMethod.POST)
    public Order createOrder(@PathVariable(value = "goods_id") String goodsId) {
        return orderService.createOrderWithRedisLock(goodsId);
    }

}
