package com.onlineshop.controller;

import com.onlineshop.model.dto.ProductsDTO;
import com.onlineshop.model.entity.User;
import com.onlineshop.service.CartService;
import com.onlineshop.service.ProductService;
import com.onlineshop.service.UserService;
import com.onlineshop.service.impl.CartServiceImpl;
import com.onlineshop.service.impl.ProductServiceImpl;
import com.onlineshop.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CartController.class)
public class CartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private ProductService productService;

    @MockBean
    private UserService userService;

    private User testUser;
    private ProductsDTO product1;
    private ProductsDTO product2;

    @BeforeEach
    public void setup() {
        testUser = new User();
        testUser.setUsername("testuser");

        product1 = new ProductsDTO();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(10.0);

        product2 = new ProductsDTO();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(15.0);
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testViewCart() throws Exception {
        List<ProductsDTO> cartItems = Arrays.asList(product1, product2);

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(cartService.getCartItemsForUser(testUser.getUsername())).thenReturn(cartItems);

        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("cartItems"))
                .andExpect(model().attributeExists("totalPrice"))
                .andExpect(model().attribute("cartItems", cartItems))
                .andExpect(model().attribute("totalPrice", 25.0));

        verify(userService, times(1)).getCurrentUser();
        verify(cartService, times(1)).getCartItemsForUser(testUser.getUsername());
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testAddToCart() throws Exception {
        when(productService.addToCart(1L)).thenReturn(true);

        mockMvc.perform(post("/cart/add")
                        .param("productId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        verify(productService, times(1)).addToCart(1L);
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testAddToCartProductUnavailable() throws Exception {
        when(productService.addToCart(1L)).thenReturn(false);

        mockMvc.perform(post("/cart/add")
                        .param("productId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product-unavailable"))
                .andExpect(flash().attributeExists("error"));

        verify(productService, times(1)).addToCart(1L);
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testRemoveProductFromCart() throws Exception {
        mockMvc.perform(delete("/cart/remove/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cartService, times(1)).removeProductFromCart(1L);
    }
}
