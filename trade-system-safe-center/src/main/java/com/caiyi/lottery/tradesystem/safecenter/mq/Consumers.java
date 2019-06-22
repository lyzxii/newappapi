package com.caiyi.lottery.tradesystem.safecenter.mq;

import com.caiyi.lottery.tradesystem.base.RollbackDTO;
import com.caiyi.lottery.tradesystem.safecenter.config.AmqpConfig;
import com.caiyi.lottery.tradesystem.safecenter.service.RollBackService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.utils.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 安全中心，消息消费者,手动回调
 *
 * @author GJ
 * @create 2017-12-29 11:30
 **/
@Slf4j
@Configuration
@AutoConfigureAfter(AmqpConfig.class)
public class Consumers {

    @Autowired
    private RollBackService rollBackService;

    @Value("${rabbitmq.localqueue}")
    private String queue;


    @Bean
    public MessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queue);
        //当前消费线程
        container.setConcurrentConsumers(5);
        //最大消费线程
        container.setMaxConcurrentConsumers(10);

        container.setMessageListener(ackListener());
        //手动确认
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return container;
    }


    @Bean
    public ChannelAwareMessageListener ackListener() {
        return new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                log.info("Consumers consume : " + message);
                List<RollbackDTO> rollbackDTOList = (List<RollbackDTO>) SerializationUtils.deserialize(message.getBody());
                for (RollbackDTO rollbackDTO : rollbackDTOList) {
                    rollBackService.transactionalCompensateSafeCenter(rollbackDTO);
                }
                //通过设置，查看控制台中队列中消息是否被消费
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);//确认消息成功消费
            }
        };
    }

}

