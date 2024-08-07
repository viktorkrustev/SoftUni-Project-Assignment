package com.onlineShop.service;

import com.onlineShop.model.dto.AddProductDTO;
import com.onlineShop.model.dto.ProductViewAdminDTO;
import com.onlineShop.model.dto.ProductsDTO;
import com.onlineShop.model.entity.Product;

import java.util.List;

public interface ProductService {
    List<ProductsDTO> getAllProducts();

    boolean addToCart(Long productId);

    Product saveProduct(Product product);

    void deleteProduct(Long id);

    Product getProductById(Long id);

    void addProduct(AddProductDTO addProductDTO);

    Product findById(Long id);

    List<ProductsDTO> filterProductsByCategory(List<ProductsDTO> products, String category);

    List<ProductsDTO> sortProducts(List<ProductsDTO> products, String sort);

    List<ProductsDTO> searchProductsByName(String name);

    Product updateProduct(Long productId, ProductViewAdminDTO productDTO);
}
