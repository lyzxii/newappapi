package com.caiyi.lottery.tradesystem.usercenter.mq;

import com.caiyi.lottery.tradesystem.usercenter.service.RollBackService;
import com.caiyi.lottery.tradesystem.base.RollbackDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.utils.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户中心消息消费者
 *
 * @author GJ
 * @create 2018-01-02 15:12
 **/
@Slf4j
@EnableRabbit
@Component
public class UserCenterConsumers {
    @Autowired
    private RollBackService rollBackService;
    @RabbitListener(queues = "lottery_queue_usercenter")
    public void processQueue(Message message) {
//        log.info("UserCenterConsumers consume : " + message);
//        List<RollbackDTO> rollbackDTOList = (List<RollbackDTO>) SerializationUtils.deserialize(message.getBody());
//        for (RollbackDTO rollbackDTO : rollbackDTOList) {
//        //    rollBackService.transactionalCompensateSafeCenter(rollbackDTO);
//        }

    }
}
