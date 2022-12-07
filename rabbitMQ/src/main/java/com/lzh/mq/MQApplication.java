package com.lzh.mq;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class MQApplication {
    public static void main(String[] args) {
        SpringApplication.run(MQApplication.class,args);
    }
}
