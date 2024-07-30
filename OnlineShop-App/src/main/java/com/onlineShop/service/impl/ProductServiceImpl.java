package com.onlineshop.service.impl;

import com.onlineshop.model.dto.AddProductDTO;
import com.onlineshop.model.dto.ProductViewAdminDTO;
import com.onlineshop.model.dto.ProductsDTO;
import com.onlineshop.model.entity.Category;
import com.onlineshop.model.entity.Product;
import com.onlineshop.repository.ProductRepository;
import com.onlineshop.service.CartService;
import com.onlineshop.service.ProductService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CartService cartService, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ProductsDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> new ProductsDTO(product.getId(), product.getName(), product.getPrice(), product.getImageUrl(), product.getBrand(), product.getCategory()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean addToCart(Long productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null || product.getStockQuantity() <= 0) {
            return false;
        }

        product.setStockQuantity(product.getStockQuantity() - 1);
        productRepository.save(product);

        cartService.addProductToCart(product);

        return true;
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public void addProduct(AddProductDTO addProductDTO) {
        Product product = modelMapper.map(addProductDTO, Product.class);
        productRepository.save(product);
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public List<ProductsDTO> filterProductsByCategory(List<ProductsDTO> products, String category) {
        if (category != null && !category.equalsIgnoreCase("ALL")) {
            try {
                Category categoryEnum = Category.valueOf(category.trim().toUpperCase());
                return products.stream()
                        .filter(p -> p.getCategory() == categoryEnum)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return products;
            }
        }
        return products;
    }

    @Override
    public List<ProductsDTO> sortProducts(List<ProductsDTO> products, String sort) {
        if (sort != null) {
            if (sort.equals("asc")) {
                return products.stream()
                        .sorted(Comparator.comparingDouble(ProductsDTO::getPrice))
                        .collect(Collectors.toList());
            } else if (sort.equals("desc")) {
                return products.stream()
                        .sorted((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()))
                        .collect(Collectors.toList());
            }
        }
        return products;
    }

    @Override
    public List<ProductsDTO> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream().map(p -> modelMapper.map(p, ProductsDTO.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Product updateProduct(Long productId, ProductViewAdminDTO productDTO) {
        Product existingProduct = productRepository.findById(productId).orElse(null);
        if (existingProduct != null) {
            existingProduct.setImageUrl(productDTO.getImageUrl());
            existingProduct.setName(productDTO.getName());
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setBrand(productDTO.getBrand());
            existingProduct.setDescription(productDTO.getDescription());
            return productRepository.save(existingProduct);
        }
        return null;
    }
}
