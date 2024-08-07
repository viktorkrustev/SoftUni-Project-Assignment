package com.onlineshop.listener;

import com.onlineshop.event.UserRegistrationEvent;
import com.onlineshop.service.impl.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationListener implements ApplicationListener<UserRegistrationEvent> {

    private final EmailService emailService;

    @Autowired
    public UserRegistrationListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void onApplicationEvent(UserRegistrationEvent event) {
        String userEmail = event.getUserEmail();
        emailService.sendRegistrationConfirmationEmail(userEmail);
    }
}