package com.onlineShop.service.impl;

import com.onlineShop.model.dto.OrderDTO;
import com.onlineShop.model.dto.ProductsDTO;
import com.onlineShop.model.entity.Order;
import com.onlineShop.model.entity.Product;
import com.onlineShop.model.entity.User;
import com.onlineShop.repository.OrderRepository;
import com.onlineShop.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductServiceImpl productService;
    private final EmailService emailService;
    private final UserServiceImpl userService;

    public OrderServiceImpl(OrderRepository orderRepository, ProductServiceImpl productService, @Lazy EmailService emailService, UserServiceImpl userService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.emailService = emailService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public Order createOrder(User user, List<ProductsDTO> cartItems, double totalPrice, OrderDTO orderDTO) {
        Order order = new Order();
        order.setUser(user);
        order.setProducts(convertToProducts(cartItems));
        order.setTotalAmount(totalPrice);
        order.setOrderDate(LocalDate.now());
        order.setStatus("Ordered");
        order.setDeliveryAddress(orderDTO.getDeliveryAddress());
        order.setContactPhone(orderDTO.getContactPhone());

        Order savedOrder = orderRepository.save(order);


        emailService.sendOrderConfirmationEmail(
                user.getEmail(),
                String.valueOf(savedOrder.getOrderDate()),
                savedOrder.getTotalAmount(),
                savedOrder.getDeliveryAddress(),
                savedOrder.getContactPhone()
        );

        return savedOrder;
    }


    @Override
    public List<Product> convertToProducts(List<ProductsDTO> dtos) {
        List<Product> products = new ArrayList<>();

        for (ProductsDTO dto : dtos) {
            Product product = productService.findById(dto.getId());
            if (product != null && product.getStockQuantity() > 0) {
                product.setStockQuantity(product.getStockQuantity() - 1);
                products.add(product);
            } else {
                products.add(null);
            }
        }

        return products;
    }



    @Override
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);

    }

    @Override
    public List<Order> findOrderByOrderDate(LocalDate today) {
        return orderRepository.findByOrderDate(today);
    }

    @Override
    public List<Order> getOrdersForCurrentUser() {
        User user = userService.getCurrentUser();
        return getOrdersByUser(user);
    }
}