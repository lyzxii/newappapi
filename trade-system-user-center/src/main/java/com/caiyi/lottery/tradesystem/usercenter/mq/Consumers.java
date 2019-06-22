package com.caiyi.lottery.tradesystem.usercenter.mq;

import com.caiyi.lottery.tradesystem.usercenter.service.RollBackService;
import com.caiyi.lottery.tradesystem.base.RollbackDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.utils.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 消息消费者
 *
 * @author GJ
 * @create 2017-12-29 10:38
 **/
@Slf4j
//@Component
//@EnableRabbit
/*@RabbitListener(containerFactory = "rabbitListenerContainerFactory", bindings = @QueueBinding(
        value = @Queue(value = "${mq.config.queue}", durable = "true"),
        exchange = @Exchange(value = "${mq.config.exchange}", type = ExchangeTypes.TOPIC),
        key = "${mq.config.key}"), admin = "rabbitAdmin")*/
public class Consumers {
    @Autowired
    private RollBackService rollBackService;
    @RabbitHandler
    public void process(Message message) {
        log.info("Consumers consume : " + message);
        List<RollbackDTO> rollbackDTOList = (List<RollbackDTO>) SerializationUtils.deserialize(message.getBody());
        for (RollbackDTO rollbackDTO : rollbackDTOList) {
       //     rollBackService.transactionalCompensate(rollbackDTO);
        }
    }
}
