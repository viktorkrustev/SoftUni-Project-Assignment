package com.onlineshop.controller;

import com.onlineshop.model.dto.*;
import com.onlineshop.model.entity.Category;
import com.onlineshop.model.entity.Product;
import com.onlineshop.model.entity.Review;
import com.onlineshop.model.entity.User;
import com.onlineshop.service.CartService;
import com.onlineshop.service.ProductService;
import com.onlineshop.service.ReviewService;
import com.onlineshop.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProductController {
    private final ProductService productService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final ReviewService reviewService;


    @Autowired
    public ProductController(ProductService productService, UserService userService,  ModelMapper modelMapper, ReviewService reviewService) {
        this.productService = productService;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.reviewService = reviewService;
    }

    @GetMapping("/products")
    public String getProducts(Model model,
                              @RequestParam(name = "name", required = false, defaultValue = "") String name,
                              @RequestParam(required = false) String sort,
                              @RequestParam(required = false) String category,
                              @AuthenticationPrincipal Principal principal) {

        List<ProductsDTO> products;

        if (name != null && !name.isEmpty()) {
            products = productService.searchProductsByName(name);
        } else {
            products = productService.getAllProducts();
        }

        products = productService.filterProductsByCategory(products, category);
        products = productService.sortProducts(products, sort);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("products", products);
        model.addAttribute("isAdmin", userService.isAdmin(authentication));
        model.addAttribute("currentCategory", category != null ? category : "ALL");
        model.addAttribute("currentSort", sort);

        return "products";
    }


    @GetMapping("/products/add-product")
    public String showAddProductForm(Model model) {
        model.addAttribute("addProductDTO", new AddProductDTO());
        return "add-product";
    }

    @PostMapping("/products/add-product")
    public String addProduct(@ModelAttribute AddProductDTO addProductDTO) {
        productService.addProduct(addProductDTO);
        return "redirect:/products";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("productId") Long productId, RedirectAttributes redirectAttributes) {
        boolean addedToCart = productService.addToCart(productId);
        if (!addedToCart) {
            redirectAttributes.addFlashAttribute("error", "Sorry, this product is currently out of stock.");
            return "redirect:/product-unavailable";
        }
        return "redirect:/products";
    }

    @GetMapping("/product-unavailable")
    public String showProductUnavailablePage() {
        return "product-unavailable";
    }

    @Transactional
    @GetMapping("/products/{id}")
    public String getProductById(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);

        if (product == null) {
            return "redirect:/products";
        }
        model.addAttribute("Product", Product.class);

        ProductViewDTO productDTO = modelMapper.map(product, ProductViewDTO.class);
        model.addAttribute("product", productDTO);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = userService.isAdmin(authentication);
        model.addAttribute("isAdmin", isAdmin);

        model.addAttribute("reviewDTO", new ReviewViewDTO());

        model.addAttribute("productId", id);

        List<ReviewViewDTO> reviews = reviewService.getReviewsForProduct(id);
        model.addAttribute("reviews", reviews);

        return "product";
    }

    @GetMapping("/products/{productId}/edit")
    public String showEditProductForm(@PathVariable Long productId, Model model) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            return "redirect:/products";
        }
        ProductViewAdminDTO productDTO = modelMapper.map(product, ProductViewAdminDTO.class);
        model.addAttribute("product", productDTO);
        return "edit-product";
    }

    @PostMapping("/products/{productId}/edit")
    public String editProduct(@PathVariable Long productId, @ModelAttribute("product") ProductViewAdminDTO productDTO) {
        Product existingProduct = productService.getProductById(productId);
        if (existingProduct == null) {
            return "redirect:/products";
        }
        existingProduct.setImageUrl(productDTO.getImageUrl());
        existingProduct.setName(productDTO.getName());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setBrand(productDTO.getBrand());
        existingProduct.setDescription(productDTO.getDescription());

        productService.saveProduct(existingProduct);

        return "redirect:/products/" + productId;
    }

    @PostMapping("/products/{productId}/delete")
    public String deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return "redirect:/products";
    }
}
