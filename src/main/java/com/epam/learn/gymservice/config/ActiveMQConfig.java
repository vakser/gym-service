package com.epam.learn.gymservice.config;

import jakarta.jms.Queue;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActiveMQConfig {
    public static final String WORKLOAD_QUEUE = "trainer.workload.queue";
    public static final String DEAD_LETTER_QUEUE = "trainer.workload.dlq";

    @Bean
    public Queue workloadQueue() {
        return new ActiveMQQueue(WORKLOAD_QUEUE);
    }

    @Bean
    public Queue deadLetterQueue() {
        return new ActiveMQQueue(DEAD_LETTER_QUEUE);
    }
}
