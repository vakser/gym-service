package com.epam.learn.gymservice.feign;

import com.epam.learn.gymservice.dto.TrainerWorkloadRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "trainer-workload-service", path = "/api/workload/update")
public interface TrainerWorkloadServiceClient {
    @PostMapping
    void updateTrainerWorkload(@RequestBody TrainerWorkloadRequest workloadRequest);
}
