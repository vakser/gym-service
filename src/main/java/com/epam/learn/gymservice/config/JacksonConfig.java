package com.epam.learn.gymservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Support for Java 8 date/time types
        return objectMapper;
    }

    @Bean
    public MappingJackson2MessageConverter messageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper); // Use the custom ObjectMapper
        converter.setTargetType(MessageType.TEXT); // Set the message type to text
        converter.setTypeIdPropertyName("_type"); // Set the custom type ID property

        // Define custom type mapping
        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        typeIdMappings.put("TrainerWorkloadRequest", com.epam.learn.gymservice.dto.TrainerWorkloadRequest.class); // Map alias to class
        converter.setTypeIdMappings(typeIdMappings);

        return converter;
    }
}
