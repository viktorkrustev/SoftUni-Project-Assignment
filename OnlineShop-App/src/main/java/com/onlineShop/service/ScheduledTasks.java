package com.onlineshop.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private final EmailService emailService;

    public ScheduledTasks(EmailService emailService) {
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 21 18 * * ?")
    public void reportDailyOrders() {
        emailService.sendReportToAdmins();
    }
}
