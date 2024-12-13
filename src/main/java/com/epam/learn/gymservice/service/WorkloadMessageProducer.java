package com.epam.learn.gymservice.service;

import com.epam.learn.gymservice.config.ActiveMQConfig;
import com.epam.learn.gymservice.dto.TrainerWorkloadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkloadMessageProducer {
    private final JmsTemplate jmsTemplate;

    public void sendWorkloadMessage(TrainerWorkloadRequest workloadRequest) {
        jmsTemplate.convertAndSend(ActiveMQConfig.WORKLOAD_QUEUE, workloadRequest);
    }
}
