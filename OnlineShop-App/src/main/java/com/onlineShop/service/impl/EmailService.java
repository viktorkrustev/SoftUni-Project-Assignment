package com.onlineshop.service.impl;

import com.onlineshop.model.entity.Order;
import com.onlineshop.model.entity.Role;
import com.onlineshop.model.entity.User;
import com.onlineshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private static final String FROM_ADDRESS = "online_store123@abv.bg";
    private final UserServiceImpl userService;
    private final OrderService orderService;

    @Autowired
    public EmailService(JavaMailSender mailSender, UserServiceImpl userService, @Lazy OrderService orderService) {
        this.mailSender = mailSender;
        this.userService = userService;
        this.orderService = orderService;
    }

    public void sendOrderConfirmationEmail(String to, String orderDate, double totalAmount, String deliveryAddress, String contactPhone) {
        String subject = "Order Confirmation";
        User user = userService.findUserByEmail(to);
        String text = String.format(
                "Dear " + user.getFirstName() + " " + user.getLastName() + ",\n\n" +
                        "Thank you for your order! Here are the details:\n\n" +
                        "Order Date: %s\n" +
                        "Total Amount: $%.2f\n" +
                        "Delivery Address: %s\n" +
                        "Contact Phone: %s\n\n" +
                        "We will notify you once your order is shipped.\n\n" +
                        "Best regards,\n" +
                        "Online Store",
                orderDate, totalAmount, deliveryAddress, contactPhone
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_ADDRESS);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendReportToAdmins() {
        LocalDate today = LocalDate.now();
        List<User> admins = userService.findUserByRole(Role.ADMIN);

        if (!admins.isEmpty()) {
            List<Order> orders = orderService.findOrderByOrderDate(today);

            double totalAmount = orders.stream()
                    .mapToDouble(Order::getTotalAmount)
                    .sum();

            String subject = "Daily Order Report";
            StringBuilder text = new StringBuilder();
            text.append("Daily Order Report for ").append(today).append(":\n\n");

            for (Order order : orders) {
                text.append("Order ID: ").append(order.getId()).append(", ")
                        .append("Total Amount: $").append(order.getTotalAmount()).append("\n");
            }

            text.append("\nTotal Amount for the Day: $").append(String.format("%.2f", totalAmount));

            for (User admin : admins) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(FROM_ADDRESS);
                message.setTo(admin.getEmail());
                message.setSubject(subject);
                message.setText(text.toString());
                mailSender.send(message);
            }
        } else {
            System.out.println("No admin users found.");
        }
    }
}
