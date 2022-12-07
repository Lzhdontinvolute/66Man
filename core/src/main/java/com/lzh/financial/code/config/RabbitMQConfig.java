package com.lzh.financial.code.config;


import org.springframework.amqp.core.Queue;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public Queue emailQueue(){
        return new Queue("send.mail");
    }


}
