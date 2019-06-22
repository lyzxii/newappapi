package com.caiyi.lottery.tradesystem.usercenter.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * rabbitmq配置
 *
 * @author GJ
 * @create 2017-12-29 9:38
 **/
@Configuration
public class AmqpConfig {
   /* @Value("${rabbitmq.host}")
    private String host;

    @Value("${rabbitmq.port}")
    private int port;

    @Value("${rabbitmq.username}")
    private String username;

    @Value("${rabbitmq.password}")
    private String password;*/

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Value("${rabbitmq.queue}")
    private String queue;

    @Value("${rabbitmq.routing.safecenterkey}")
    private String safecenterRoutingKey;

    @Value("${rabbitmq.safecenterqueue}")
    private String safecenterQueue;

    @Value("${rabbitmq.localexchange}")
    private String localexchange;

    @Value("${rabbitmq.routing.localkey}")
    private String localkey;

    @Value("${rabbitmq.localqueue}")
    private String localqueue;
    /**
     * 配置链接信息
     * @return
     */
    /*@Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host,port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPublisherConfirms(true); // 必须要设置
        return connectionFactory;
    }*/

    /**
     * 创建 RabbitAdmin
     * @param connectionFactory
     * @return
     * @throws Exception
     */
    /*@Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) throws Exception {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        return rabbitAdmin;
    }*/

    /**
     * 配置消息交换机
     * 针对消费者配置
     FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     HeadersExchange ：通过添加属性key-value匹配
     DirectExchange:按照routingkey分发到指定队列
     TopicExchange:多关键字匹配
     */
    @Bean
    public DirectExchange defaultExchange() {
        return new DirectExchange(exchange, true, false);
    }

    /**
     * 配置消息队列
     * 针对消费者配置
     * @return
     */
    @Bean
    public Queue queue() {
        return new Queue(queue, true); //队列持久

    }

    /**
     * 将消息队列与交换机绑定
     * 针对消费者配置
     * @return
     */
    @Bean
    public Binding bindingUserCenterInfo() {
        return BindingBuilder.bind(queue()).to(defaultExchange()).with(routingKey);
    }

    @Bean
    public Queue safecenterQueue() {
        return new Queue(safecenterQueue, true); //队列持久

    }
    @Bean
    public Binding bindingSafecenterInfo() {
        return BindingBuilder.bind(safecenterQueue()).to(defaultExchange()).with(safecenterRoutingKey);
    }

    @Bean
    public DirectExchange localExchange() {
        return new DirectExchange(localexchange, true, false);
    }

    @Bean
    public Queue localQueue() {
        return new Queue(localqueue, true); //队列持久

    }
    @Bean
    public Binding bindinglocalInfo() {
        return BindingBuilder.bind(localQueue()).to(localExchange()).with(localkey);
    }


    /**
     * 因为要设置回调类，所以应是prototype类型，
     * 如果需要在生产者需要消息发送后的回调，需要对rabbitTemplate设置ConfirmCallback对象，由于不同的生产者需要对应不同的ConfirmCallback，
     * 如果rabbitTemplate设置为单例bean(singleton类型)，则所有的rabbitTemplate
     * 实际的ConfirmCallback为最后一次申明的ConfirmCallback。
     * @return
     */
    /*@Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate newRabbitTemplate(){
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        return template;
    }*/
}
