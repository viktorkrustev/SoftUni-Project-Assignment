package com.onlineshop.repository;

import com.onlineshop.model.entity.Cart;
import com.onlineshop.model.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findCartsByUserUsername(String username);

    Optional<Cart> findCartByUserUsername(String username);

    @Transactional
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.id = :cartId")
    void deleteAllProductsFromCartByCartId(@Param("cartId") Long cartId);

}
