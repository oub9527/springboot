package com.ob.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Author: oubin
 * @Date: 2019/6/1 14:00
 * @Description:
 */
@Slf4j
@RabbitListener(queues = "oqueue")
@Component
public class Consumer {

    @RabbitHandler
    public void process(String message) {
        log.info("收到的消息为: {}" + message);
    }
}
