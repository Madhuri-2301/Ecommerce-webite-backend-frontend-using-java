package com.ecommerce.controller;

import com.ecommerce.model.Cart;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Cart> getUserCart(@RequestAttribute Long userId) {
        return cartRepository.findByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE);
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<?> addToCart(@RequestAttribute Long userId, 
                                     @PathVariable Long productId,
                                     @RequestParam(defaultValue = "1") Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStockQuantity() < quantity) {
            return ResponseEntity.badRequest().body("Insufficient stock");
        }

        Cart cart = cartRepository.findByUserAndProductAndStatus(user, product, Cart.CartStatus.ACTIVE)
                .orElse(new Cart());

        cart.setUser(user);
        cart.setProduct(product);
        cart.setQuantity(cart.getQuantity() == null ? quantity : cart.getQuantity() + quantity);
        cart.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));

        cartRepository.save(cart);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/update/{cartId}")
    public ResponseEntity<?> updateCartItem(@PathVariable Long cartId,
                                          @RequestParam Integer quantity) {
        return cartRepository.findById(cartId)
                .map(cart -> {
                    if (cart.getProduct().getStockQuantity() < quantity) {
                        return ResponseEntity.badRequest().body("Insufficient stock");
                    }
                    cart.setQuantity(quantity);
                    cart.setTotalPrice(cart.getProduct().getPrice().multiply(BigDecimal.valueOf(quantity)));
                    cartRepository.save(cart);
                    return ResponseEntity.ok(cart);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/remove/{cartId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long cartId) {
        return cartRepository.findById(cartId)
                .map(cart -> {
                    cartRepository.delete(cart);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestAttribute Long userId) {
        List<Cart> cartItems = cartRepository.findByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE);
        
        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body("Cart is empty");
        }

        for (Cart item : cartItems) {
            Product product = item.getProduct();
            if (product.getStockQuantity() < item.getQuantity()) {
                return ResponseEntity.badRequest()
                    .body("Insufficient stock for product: " + product.getName());
            }
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
            
            item.setStatus(Cart.CartStatus.COMPLETED);
            cartRepository.save(item);
        }

        return ResponseEntity.ok().body("Order placed successfully");
    }
}
