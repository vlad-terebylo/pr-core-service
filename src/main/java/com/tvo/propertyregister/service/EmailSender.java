package com.tvo.propertyregister.service;

import com.tvo.propertyregister.model.dto.EmailEventDto;

public interface EmailSender {

    void send(EmailEventDto message);
}
