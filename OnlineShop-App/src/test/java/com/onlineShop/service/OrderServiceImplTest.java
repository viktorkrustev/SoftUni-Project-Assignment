package com.onlineshop.service;

import com.onlineshop.model.dto.OrderDTO;
import com.onlineshop.model.dto.ProductsDTO;
import com.onlineshop.model.entity.Order;
import com.onlineshop.model.entity.Product;
import com.onlineshop.model.entity.User;
import com.onlineshop.repository.OrderRepository;
import com.onlineshop.service.OrderService;
import com.onlineshop.service.impl.EmailService;
import com.onlineshop.service.impl.OrderServiceImpl;
import com.onlineshop.service.impl.ProductServiceImpl;
import com.onlineshop.service.impl.UserServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductServiceImpl productService;

    @Mock
    private EmailService emailService;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Product product;
    private ProductsDTO productsDTO;
    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("testuser@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        product = new Product();
        product.setId(1L);
        product.setStockQuantity(10);

        productsDTO = new ProductsDTO();
        productsDTO.setId(1L);

        orderDTO = new OrderDTO();
        orderDTO.setDeliveryAddress("123 Main St");
        orderDTO.setContactPhone("123-456-7890");
    }

    @Test
    @Transactional
    void createOrder() {
        List<ProductsDTO> cartItems = Collections.singletonList(productsDTO);
        double totalPrice = 100.0;

        when(productService.findById(productsDTO.getId())).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order createdOrder = orderService.createOrder(user, cartItems, totalPrice, orderDTO);

        assertNotNull(createdOrder);
        assertEquals(user, createdOrder.getUser());
        assertEquals(totalPrice, createdOrder.getTotalAmount());
        assertEquals(orderDTO.getDeliveryAddress(), createdOrder.getDeliveryAddress());
        assertEquals(orderDTO.getContactPhone(), createdOrder.getContactPhone());

        verify(emailService, times(1)).sendOrderConfirmationEmail(
                eq(user.getEmail()),
                anyString(),
                eq(totalPrice),
                eq(orderDTO.getDeliveryAddress()),
                eq(orderDTO.getContactPhone())
        );

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void convertToProducts() {
        List<ProductsDTO> dtos = Collections.singletonList(productsDTO);

        when(productService.findById(productsDTO.getId())).thenReturn(product);

        List<Product> products = orderService.convertToProducts(dtos);

        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals(product, products.get(0));
        assertEquals(9, product.getStockQuantity());

        when(productService.findById(productsDTO.getId())).thenReturn(null);

        products = orderService.convertToProducts(dtos);

        assertNotNull(products);
        assertEquals(1, products.size());
        assertNull(products.get(0));

        product.setStockQuantity(0);
        when(productService.findById(productsDTO.getId())).thenReturn(product);

        products = orderService.convertToProducts(dtos);

        assertNotNull(products);
        assertEquals(1, products.size());
        assertNull(products.get(0));
    }

    @Test
    void getOrdersByUser() {
        List<Order> orders = new ArrayList<>();
        when(orderRepository.findByUser(user)).thenReturn(orders);

        List<Order> returnedOrders = orderService.getOrdersByUser(user);

        assertEquals(orders, returnedOrders);
        verify(orderRepository, times(1)).findByUser(user);
    }

    @Test
    void findOrderByOrderDate() {
        LocalDate today = LocalDate.now();
        List<Order> orders = new ArrayList<>();
        when(orderRepository.findByOrderDate(today)).thenReturn(orders);

        List<Order> returnedOrders = orderService.findOrderByOrderDate(today);

        assertEquals(orders, returnedOrders);
        verify(orderRepository, times(1)).findByOrderDate(today);
    }

    @Test
    void getOrdersForCurrentUser() {
        when(userService.getCurrentUser()).thenReturn(user);
        List<Order> orders = new ArrayList<>();
        when(orderRepository.findByUser(user)).thenReturn(orders);

        List<Order> returnedOrders = orderService.getOrdersForCurrentUser();

        assertEquals(orders, returnedOrders);
        verify(userService, times(1)).getCurrentUser();
        verify(orderRepository, times(1)).findByUser(user);
    }
}
