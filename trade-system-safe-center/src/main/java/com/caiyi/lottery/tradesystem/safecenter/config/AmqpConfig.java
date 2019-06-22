package com.caiyi.lottery.tradesystem.safecenter.config;

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
   /* @Bean
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
   /* @Bean
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
     * 如果是singleton类型，则回调类为最后一次设置
     * @return
     */
   /* @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate newRabbitTemplate(){
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        return template;
    }*/
}
