package com.onlineshop.service;

import com.onlineshop.model.dto.OrderDTO;
import com.onlineshop.model.dto.ProductsDTO;
import com.onlineshop.model.entity.Order;
import com.onlineshop.model.entity.Product;
import com.onlineshop.model.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    Order createOrder(User user, List<ProductsDTO> cartItems, double totalPrice, OrderDTO orderDTO);

    List<Product> convertToProducts(List<ProductsDTO> dtos);

    List<Order> getOrdersByUser(User user);

    List<Order> findOrderByOrderDate(LocalDate today);

    List<Order> getOrdersForCurrentUser();
}
