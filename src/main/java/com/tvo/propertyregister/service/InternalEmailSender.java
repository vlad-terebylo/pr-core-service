package com.tvo.propertyregister.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvo.propertyregister.model.dto.EmailEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.tvo.propertyregister.service.utils.Constants.EMAIL_TOPIC;

@Service
@RequiredArgsConstructor
public class InternalEmailSender implements EmailSender {

    private final ObjectMapper mapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void send(EmailEventDto message) {
        try {
            String body = mapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(EMAIL_TOPIC, body);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }
}
