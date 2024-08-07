package com.onlineShop.service;

import com.onlineShop.model.dto.ProductsDTO;
import com.onlineShop.model.entity.Cart;
import com.onlineShop.model.entity.Product;

import java.util.List;

public interface CartService {
    void addProductToCart(Product product);

    List<ProductsDTO> getCartItemsForUser(String username);

    void removeProductFromCart(Long productId);

    void deleteAllProductsFromCart(Long cartId);

    Cart findCartByUsername(String username);
}
