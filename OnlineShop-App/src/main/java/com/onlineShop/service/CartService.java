package com.onlineshop.service;

import com.onlineshop.model.dto.ProductsDTO;
import com.onlineshop.model.entity.Cart;
import com.onlineshop.model.entity.Product;

import java.util.List;

public interface CartService {
    void addProductToCart(Product product);

    List<ProductsDTO> getCartItemsForUser(String username);

    void removeProductFromCart(Long productId);

    void deleteAllProductsFromCart(Long cartId);

    Cart findCartByUsername(String username);

}
