package com.caiyi.lottery.tradesystem.usercenter.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * 消息生产者
 *
 * @author GJ
 * @create 2017-12-29 10:37
 **/
@Slf4j
@Component
public class Producers implements  RabbitTemplate.ConfirmCallback{
    @Value("${rabbitmq.exchange}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;
    @Value("${rabbitmq.routing.safecenterkey}")
    private String safecenterRoutingKey;

    @Value("${rabbitmq.localexchange}")
    private String localexchange;

    @Value("${rabbitmq.routing.localkey}")
    private String localkey;

   // @Autowired
    private RabbitTemplate newRabbitTemplate;


    /**
     * 配置发送消息的rabbitTemplate，因为是构造方法，所以不用注解Spring也会自动注入（应该是新版本的特性）
     * @param rabbitTemplate
     */
    public Producers(RabbitTemplate rabbitTemplate){
        this.newRabbitTemplate = rabbitTemplate;
        //设置消费回调
        this.newRabbitTemplate.setConfirmCallback(this);
    }

    /**
     * 消息的回调，主要是实现RabbitTemplate.ConfirmCallback接口
     * 注意，消息回调只能代表成功消息发送到RabbitMQ服务器，不能代表消息被成功处理和接受
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String s) {
       log.info(" 回调id:" + correlationData.getId());
        if (ack) {
            log.info("MQ服务器接收消息成功");
        } else {
            log.info("MQ服务器接收消息失败:" + s);
        }
    }

    public void sendString(String msg) {
        String uuid = UUID.randomUUID().toString();
        CorrelationData correlationId = new CorrelationData(uuid);
        newRabbitTemplate.convertAndSend(exchange, safecenterRoutingKey, msg,correlationId);
    //    newRabbitTemplate.convertAndSend(exchange, routingKey, msg,correlationId);
    }

    public void sendObject(Object msg) {
        String uuid = UUID.randomUUID().toString();
        CorrelationData correlationId = new CorrelationData(uuid);
        newRabbitTemplate.convertAndSend(exchange, routingKey, msg,correlationId);
    }

    public void sendList(List msg) {
        String uuid = UUID.randomUUID().toString();
        CorrelationData correlationId = new CorrelationData(uuid);
        newRabbitTemplate.convertAndSend(exchange, routingKey, msg,correlationId);
    }

    public void sendSafeCenterList(List msg) {
        String uuid = UUID.randomUUID().toString();
        CorrelationData correlationId = new CorrelationData(uuid);
        newRabbitTemplate.convertAndSend(exchange, safecenterRoutingKey, msg,correlationId);
    }

    public void sendLocalList(List msg) {
        String uuid = UUID.randomUUID().toString();
        CorrelationData correlationId = new CorrelationData(uuid);
        newRabbitTemplate.convertAndSend(localexchange, localkey, msg,correlationId);
    }
}
