package com.onlineshop.service;

import com.onlineshop.model.dto.OrderDTO;
import com.onlineshop.model.dto.ProductsDTO;
import com.onlineshop.model.entity.Cart;
import com.onlineshop.model.entity.Order;
import com.onlineshop.model.entity.Product;
import com.onlineshop.model.entity.User;
import com.onlineshop.repository.OrderRepository;
import com.onlineshop.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final EmailService emailService;
    private final UserService userService;

    public OrderService(OrderRepository orderRepository, ProductService productService, @Lazy EmailService emailService, UserService userService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.emailService = emailService;
        this.userService = userService;
    }

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


    private List<Product> convertToProducts(List<ProductsDTO> dtos) {
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



    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);

    }

    public List<Order> findOrderByOrderDate(LocalDate today) {
        return orderRepository.findByOrderDate(today);
    }

    public List<Order> getOrdersForCurrentUser() {
        User user = userService.getCurrentUser();
        return getOrdersByUser(user);
    }
}
