package com.onlineshop.service;

import com.onlineshop.model.dto.ProductsDTO;
import com.onlineshop.model.entity.Cart;
import com.onlineshop.model.entity.Product;
import com.onlineshop.model.entity.User;
import com.onlineshop.repository.CartRepository;
import com.onlineshop.service.CartService;
import com.onlineshop.service.impl.CartServiceImpl;
import com.onlineshop.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        cart = new Cart();
        cart.setUser(user);
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        cart.setProducts(new ArrayList<>());
        user.setCart(cart);
    }

    @Test
    void addProductToCart() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.addProductToCart(product);

        assertEquals(1, user.getCart().getProducts().size());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void getCartItemsForUser() {
        cart.getProducts().add(product);
        ProductsDTO productDTO = new ProductsDTO();
        when(cartRepository.findCartsByUserUsername("testuser")).thenReturn(List.of(cart));
        when(modelMapper.map(any(Product.class), eq(ProductsDTO.class))).thenReturn(productDTO);

        List<ProductsDTO> productsDTOS = cartService.getCartItemsForUser("testuser");

        assertEquals(1, productsDTOS.size());
        verify(cartRepository, times(1)).findCartsByUserUsername("testuser");
    }

    @Test
    void removeProductFromCart() {
        cart.getProducts().add(product);
        when(userService.getCurrentUser()).thenReturn(user);
        when(cartRepository.findCartByUserUsername("testuser")).thenReturn(Optional.of(cart));

        cartService.removeProductFromCart(1L);

        assertEquals(0, cart.getProducts().size());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void deleteAllProductsFromCart() {
        cartService.deleteAllProductsFromCart(1L);
        verify(cartRepository, times(1)).deleteAllProductsFromCartByCartId(1L);
    }

    @Test
    void findCartByUsername() {
        when(cartRepository.findCartByUserUsername("testuser")).thenReturn(Optional.of(cart));

        Cart foundCart = cartService.findCartByUsername("testuser");

        assertNotNull(foundCart);
        assertEquals("testuser", foundCart.getUser().getUsername());
        verify(cartRepository, times(1)).findCartByUserUsername("testuser");
    }

    @Test
    void addProductToCartCreatesNewCartIfNotExists() {
        User newUser = new User();
        newUser.setUsername("newuser");
        when(userService.getCurrentUser()).thenReturn(newUser);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cartService.addProductToCart(product);

        assertNotNull(newUser.getCart());
        assertEquals(1, newUser.getCart().getProducts().size());
        assertEquals(product, newUser.getCart().getProducts().get(0));
        verify(cartRepository, times(1)).save(any(Cart.class));
    }
}