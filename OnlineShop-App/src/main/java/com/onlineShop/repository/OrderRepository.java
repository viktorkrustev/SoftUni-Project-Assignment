package com.onlineShop.repository;

import com.onlineShop.model.entity.Order;
import com.onlineShop.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    List<Order> findByOrderDate(LocalDate today);
}
