package com.onlineshop.controller;

import com.onlineshop.model.dto.OrderDTO;
import com.onlineshop.model.dto.ProductsDTO;
import com.onlineshop.model.entity.Cart;
import com.onlineshop.model.entity.User;
import com.onlineshop.service.CartService;
import com.onlineshop.service.OrderService;
import com.onlineshop.service.UserService;
import com.onlineshop.service.impl.CartServiceImpl;
import com.onlineshop.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(OrderController.class)
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private CartService cartService;

    @MockBean
    private OrderService orderService;

    private User testUser;
    private Cart testCart;
    private ProductsDTO product1;
    private ProductsDTO product2;

    @BeforeEach
    public void setup() {
        testUser = new User();
        testUser.setUsername("testuser");

        testCart = new Cart();
        testCart.setId(1L);
        testUser.setCart(testCart);

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
    public void testCheckout() throws Exception {
        List<ProductsDTO> cartItems = Arrays.asList(product1, product2);

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(cartService.getCartItemsForUser(testUser.getUsername())).thenReturn(cartItems);

        mockMvc.perform(get("/checkout"))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("cartItems"))
                .andExpect(model().attributeExists("totalPrice"))
                .andExpect(model().attributeExists("orderDTO"))
                .andExpect(model().attribute("user", testUser))
                .andExpect(model().attribute("cartItems", cartItems))
                .andExpect(model().attribute("totalPrice", "25.00"));

        verify(userService, times(1)).getCurrentUser();
        verify(cartService, times(1)).getCartItemsForUser(testUser.getUsername());
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testCheckout_EmptyCart() throws Exception {
        List<ProductsDTO> cartItems = Arrays.asList();

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(cartService.getCartItemsForUser(testUser.getUsername())).thenReturn(cartItems);

        mockMvc.perform(get("/checkout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(userService, times(1)).getCurrentUser();
        verify(cartService, times(1)).getCartItemsForUser(testUser.getUsername());
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testConfirmOrder_Success() throws Exception {
        List<ProductsDTO> cartItems = Arrays.asList(product1, product2);
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setDeliveryAddress("123 Test St");
        orderDTO.setContactPhone("1234567890");

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(cartService.getCartItemsForUser(testUser.getUsername())).thenReturn(cartItems);

        mockMvc.perform(post("/confirm-order")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .flashAttr("orderDTO", orderDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(orderService, times(1)).createOrder(eq(testUser), eq(cartItems), eq(25.0), eq(orderDTO));
        verify(cartService, times(1)).deleteAllProductsFromCart(anyLong());
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testConfirmOrder_ValidationError() throws Exception {
        List<ProductsDTO> cartItems = Arrays.asList(product1, product2);
        OrderDTO orderDTO = new OrderDTO();

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(cartService.getCartItemsForUser(testUser.getUsername())).thenReturn(cartItems);

        mockMvc.perform(post("/confirm-order")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .flashAttr("orderDTO", orderDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout"))
                .andExpect(model().attributeHasFieldErrors("orderDTO"));

        verify(orderService, times(0)).createOrder(any(), any(), anyDouble(), any());
        verify(cartService, times(0)).deleteAllProductsFromCart(anyLong());
    }
}