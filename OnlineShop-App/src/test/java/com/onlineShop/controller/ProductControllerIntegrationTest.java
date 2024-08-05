package com.onlineshop.controller;

import com.onlineshop.model.dto.*;
import com.onlineshop.model.entity.Product;
import com.onlineshop.service.ProductService;
import com.onlineshop.service.ReviewService;
import com.onlineshop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductController.class)
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private UserService userService;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private ModelMapper modelMapper;

    private Product product;
    private ProductViewDTO productViewDTO;
    private ProductViewAdminDTO productViewAdminDTO;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Sample Product");
        product.setImageUrl("sample-image-url.jpg");
        product.setPrice(19.99);

        productViewDTO = new ProductViewDTO();
        productViewAdminDTO = new ProductViewAdminDTO();

        when(productService.getProductById(1L)).thenReturn(product);
        when(productService.getAllProducts()).thenReturn(Collections.singletonList(new ProductsDTO()));
        when(userService.isAdmin(any())).thenReturn(true);
        when(productService.updateProduct(anyLong(), any(ProductViewAdminDTO.class))).thenReturn(product);
        when(reviewService.getReviewsForProduct(anyLong())).thenReturn(Collections.emptyList());
        when(modelMapper.map(product, ProductViewDTO.class)).thenReturn(productViewDTO);
        when(modelMapper.map(product, ProductViewAdminDTO.class)).thenReturn(productViewAdminDTO);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetProducts() throws Exception {
        // Предполага се, че productService.getAllProducts() връща лист от продукти
        when(productService.getAllProducts()).thenReturn(Collections.singletonList(new ProductsDTO()));

        mockMvc.perform(MockMvcRequestBuilders.get("/products"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("products"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("products"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("isAdmin"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("currentCategory"))
                .andExpect(MockMvcResultMatchers.model().attribute("currentSort", ""))
                .andExpect(MockMvcResultMatchers.model().attribute("currentCategory", "ALL"));

        verify(productService, times(1)).getAllProducts();
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void testShowAddProductForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/products/add-product"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("add-product"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("addProductDTO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddProduct() throws Exception {
        AddProductDTO addProductDTO = new AddProductDTO();

        mockMvc.perform(MockMvcRequestBuilders.post("/products/add-product")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .flashAttr("addProductDTO", addProductDTO))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/products"));

        verify(productService, times(1)).addProduct(any(AddProductDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetProductById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/products/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("product"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("product"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("isAdmin"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("reviewDTO"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("reviews"));

        verify(productService, times(1)).getProductById(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testShowEditProductForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/products/1/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("edit-product"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("product"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testEditProduct() throws Exception {
        ProductViewAdminDTO productDTO = new ProductViewAdminDTO();

        mockMvc.perform(MockMvcRequestBuilders.post("/products/1/edit")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .flashAttr("product", productDTO))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/products/1"));

        verify(productService, times(1)).updateProduct(anyLong(), any(ProductViewAdminDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteProduct() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/products/1/delete")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/products"));

        verify(productService, times(1)).deleteProduct(anyLong());
    }
}
