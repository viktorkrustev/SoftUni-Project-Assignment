package com.onlineshop.service.impl;

import com.onlineshop.model.dto.ProductsDTO;
import com.onlineshop.model.entity.Cart;
import com.onlineshop.model.entity.Product;
import com.onlineshop.model.entity.User;
import com.onlineshop.repository.CartRepository;
import com.onlineshop.service.CartService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserServiceImpl userService;
    private final ModelMapper modelMapper;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, UserServiceImpl userService, ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public void addProductToCart(Product product) {
        User user = userService.getCurrentUser();
        Cart cart = user.getCart();
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            user.setCart(cart);
        }
        if (cart.getProducts() == null) {
            cart.setProducts(new ArrayList<>());
        }
        cart.getProducts().add(product);
        cartRepository.save(cart);
    }


    @Transactional
    public List<ProductsDTO> getCartItemsForUser(String username) {
        List<Cart> carts = cartRepository.findCartsByUserUsername(username);
        return carts.stream()
                .flatMap(cart -> cart.getProducts().stream())
                .map(product -> modelMapper.map(product, ProductsDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeProductFromCart(Long productId) {
        User user = userService.getCurrentUser();

        Cart cart = cartRepository.findCartByUserUsername(user.getUsername()).orElse(null);

        Product productToRemove = cart.getProducts()
                .stream()
                .filter(product -> product.getId().equals(productId))
                .findFirst()
                .orElse(null);

        cart.getProducts().remove(productToRemove);

        cartRepository.save(cart);
    }

    @Transactional
    public void deleteAllProductsFromCart(Long cartId) {
        cartRepository.deleteAllProductsFromCartByCartId(cartId);
    }

    public Cart findCartByUsername(String username) {
        return cartRepository.findCartByUserUsername(username).orElse(null);
    }

}