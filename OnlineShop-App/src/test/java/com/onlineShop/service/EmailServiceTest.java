package com.onlineShop.service;

import com.onlineShop.model.entity.Order;
import com.onlineShop.model.entity.Role;
import com.onlineShop.model.entity.User;
import com.onlineShop.service.impl.EmailService;
import com.onlineShop.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private EmailService emailService;

    private User user;
    private Order order;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("testuser@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(Role.USER);

        order = new Order();
        order.setId(1L);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(100.0);
    }

    @Test
    void sendOrderConfirmationEmail() {
        when(userService.findUserByEmail("testuser@example.com")).thenReturn(user);

        emailService.sendOrderConfirmationEmail("testuser@example.com", "2023-07-31", 100.0, "123 Main St", "123-456-7890");

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals("Order Confirmation", sentMessage.getSubject());
        assertEquals("testuser@example.com", sentMessage.getTo()[0]);
        assertEquals("online_store123@abv.bg", sentMessage.getFrom());
        assertEquals(
                "Dear John Doe,\n\n" +
                        "Thank you for your order! Here are the details:\n\n" +
                        "Order Date: 2023-07-31\n" +
                        "Total Amount: $100.00\n" +
                        "Delivery Address: 123 Main St\n" +
                        "Contact Phone: 123-456-7890\n\n" +
                        "We will notify you once your order is shipped.\n\n" +
                        "Best regards,\n" +
                        "Online Store",
                sentMessage.getText()
        );
    }

    @Test
    void sendReportToAdmins() {
        User admin = new User();
        admin.setEmail("admin@example.com");
        admin.setRole(Role.ADMIN);

        when(userService.findUserByRole(Role.ADMIN)).thenReturn(List.of(admin));
        when(orderService.findOrderByOrderDate(any(LocalDate.class))).thenReturn(List.of(order));

        emailService.sendReportToAdmins();

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals("Daily Order Report", sentMessage.getSubject());
        assertEquals("admin@example.com", sentMessage.getTo()[0]);
        assertEquals("online_store123@abv.bg", sentMessage.getFrom());
        assertEquals(
                "Daily Order Report for " + LocalDate.now() + ":\n\n" +
                        "Order ID: 1, Total Amount: $100.0\n\n" +
                        "Total Amount for the Day: $100.00",
                sentMessage.getText()
        );
    }

    @Test
    void sendReportToAdmins_noAdmins() {
        when(userService.findUserByRole(Role.ADMIN)).thenReturn(Collections.emptyList());

        emailService.sendReportToAdmins();

        verify(mailSender, times(0)).send(any(SimpleMailMessage.class));
    }
}
