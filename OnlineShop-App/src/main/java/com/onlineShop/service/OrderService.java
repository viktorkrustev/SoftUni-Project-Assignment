package com.onlineShop.service;

import com.onlineShop.model.dto.OrderDTO;
import com.onlineShop.model.dto.ProductsDTO;
import com.onlineShop.model.entity.Order;
import com.onlineShop.model.entity.Product;
import com.onlineShop.model.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    Order createOrder(User user, List<ProductsDTO> cartItems, double totalPrice, OrderDTO orderDTO);

    List<Product> convertToProducts(List<ProductsDTO> dtos);

    List<Order> getOrdersByUser(User user);

    List<Order> findOrderByOrderDate(LocalDate today);

    List<Order> getOrdersForCurrentUser();
}
