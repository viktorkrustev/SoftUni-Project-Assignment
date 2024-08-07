package com.onlineShop.service;

import com.onlineShop.model.dto.AddProductDTO;
import com.onlineShop.model.dto.ProductViewAdminDTO;
import com.onlineShop.model.dto.ProductsDTO;
import com.onlineShop.model.entity.Category;
import com.onlineShop.model.entity.Product;
import com.onlineShop.repository.ProductRepository;
import com.onlineShop.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartService cartService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductsDTO productsDTO;
    private AddProductDTO addProductDTO;
    private ProductViewAdminDTO productViewAdminDTO;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setStockQuantity(10);
        product.setImageUrl("http://example.com/image.jpg");
        product.setBrand("Test Brand");
        product.setCategory(Category.TELEVISIONS);
        product.setDescription("Test Description");

        productsDTO = new ProductsDTO();
        productsDTO.setId(1L);
        productsDTO.setName("Test Product");
        productsDTO.setPrice(100.0);
        productsDTO.setImageUrl("http://example.com/image.jpg");
        productsDTO.setBrand("Test Brand");
        productsDTO.setCategory(Category.TELEVISIONS);

        addProductDTO = new AddProductDTO();
        addProductDTO.setName("New Product");
        addProductDTO.setPrice(200.0);
        addProductDTO.setStockQuantity(20);
        addProductDTO.setImageUrl("http://example.com/new-image.jpg");
        addProductDTO.setBrand("New Brand");
        addProductDTO.setCategory(Category.WATCHES);
        addProductDTO.setDescription("New Description");

        productViewAdminDTO = new ProductViewAdminDTO();
        productViewAdminDTO.setName("Updated Product");
        productViewAdminDTO.setPrice(150.0);
        productViewAdminDTO.setImageUrl("http://example.com/updated-image.jpg");
        productViewAdminDTO.setBrand("Updated Brand");
        productViewAdminDTO.setDescription("Updated Description");

    }

    @Test
    void getAllProducts() {
        List<Product> products = Collections.singletonList(product);
        when(productRepository.findAll()).thenReturn(products);

        List<ProductsDTO> result = productService.getAllProducts();

        assertEquals(1, result.size());
        assertEquals(productsDTO.getName(), result.get(0).getName());
    }

    @Test
    void addToCart_Success() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        boolean result = productService.addToCart(product.getId());

        assertTrue(result);
        verify(cartService, times(1)).addProductToCart(product);
    }

    @Test
    void addToCart_Failure_ProductNotFound() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        boolean result = productService.addToCart(product.getId());

        assertFalse(result);
        verify(cartService, never()).addProductToCart(any());
    }

    @Test
    void addToCart_Failure_InsufficientStock() {
        product.setStockQuantity(0);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        boolean result = productService.addToCart(product.getId());

        assertFalse(result);
        verify(cartService, never()).addProductToCart(any());
    }

    @Test
    void saveProduct() {
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.saveProduct(product);

        assertEquals(product, result);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void deleteProduct() {
        productService.deleteProduct(product.getId());

        verify(productRepository, times(1)).deleteById(product.getId());
    }

    @Test
    void getProductById() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        Product result = productService.getProductById(product.getId());

        assertEquals(product, result);
    }

    @Test
    void addProduct() {
        when(modelMapper.map(addProductDTO, Product.class)).thenReturn(product);
        productService.addProduct(addProductDTO);

        verify(productRepository, times(1)).save(product);
    }

    @Test
    void findById() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        Product result = productService.findById(product.getId());

        assertEquals(product, result);
    }

    @Test
    void filterProductsByCategory_InvalidCategory() {
        List<ProductsDTO> products = Arrays.asList(productsDTO, productsDTO);
        String invalidCategory = "INVALID_CATEGORY";

        List<ProductsDTO> filteredProducts = productService.filterProductsByCategory(products, invalidCategory);

        assertEquals(products.size(), filteredProducts.size());
        assertEquals(products, filteredProducts);
    }

    @Test
    void filterProductsByCategory() {
        List<ProductsDTO> products = Arrays.asList(productsDTO, new ProductsDTO(2L, "Another Product", 50.0, "http://example.com/another-image.jpg", "Another Brand", Category.WATCHES));

        List<ProductsDTO> filteredProducts = productService.filterProductsByCategory(products, "TELEVISIONS");

        assertEquals(1, filteredProducts.size());
        assertEquals(Category.TELEVISIONS, filteredProducts.get(0).getCategory());
    }

    @Test
    void sortProducts_Ascending() {
        ProductsDTO cheaperProduct = new ProductsDTO(2L, "Cheaper Product", 50.0, "http://example.com/cheaper-image.jpg", "Cheap Brand", Category.WATCHES);
        List<ProductsDTO> products = Arrays.asList(productsDTO, cheaperProduct);

        List<ProductsDTO> sortedProducts = productService.sortProducts(products, "asc");

        assertEquals(2, sortedProducts.size());
        assertEquals(cheaperProduct, sortedProducts.get(0));
    }

    @Test
    void sortProducts_Descending() {
        ProductsDTO cheaperProduct = new ProductsDTO(2L, "Cheaper Product", 50.0, "http://example.com/cheaper-image.jpg", "Cheap Brand", Category.WATCHES);
        List<ProductsDTO> products = Arrays.asList(productsDTO, cheaperProduct);

        List<ProductsDTO> sortedProducts = productService.sortProducts(products, "desc");

        assertEquals(2, sortedProducts.size());
        assertEquals(productsDTO, sortedProducts.get(0));
    }

    @Test
    void searchProductsByName() {
        when(productRepository.findByNameContainingIgnoreCase("Test")).thenReturn(Collections.singletonList(product));
        when(modelMapper.map(product, ProductsDTO.class)).thenReturn(productsDTO);

        List<ProductsDTO> result = productService.searchProductsByName("Test");

        assertEquals(1, result.size());
        assertEquals(productsDTO.getName(), result.get(0).getName());
    }

    @Test
    void updateProduct() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        Product updatedProduct = productService.updateProduct(product.getId(), productViewAdminDTO);

        assertNotNull(updatedProduct);
        assertEquals(productViewAdminDTO.getName(), updatedProduct.getName());
        assertEquals(productViewAdminDTO.getPrice(), updatedProduct.getPrice());
        assertEquals(productViewAdminDTO.getImageUrl(), updatedProduct.getImageUrl());
        assertEquals(productViewAdminDTO.getBrand(), updatedProduct.getBrand());
        assertEquals(productViewAdminDTO.getDescription(), updatedProduct.getDescription());
    }
}
