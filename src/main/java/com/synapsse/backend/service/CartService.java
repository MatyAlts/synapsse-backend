package com.synapsse.backend.service;

import com.synapsse.backend.dto.CartItemRequest;
import com.synapsse.backend.dto.CartItemResponse;
import com.synapsse.backend.dto.CartResponse;
import com.synapsse.backend.dto.ProductSummary;
import com.synapsse.backend.entity.Cart;
import com.synapsse.backend.entity.CartItem;
import com.synapsse.backend.entity.Product;
import com.synapsse.backend.entity.User;
import com.synapsse.backend.repository.CartRepository;
import com.synapsse.backend.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    public CartService(CartRepository cartRepository, ProductRepository productRepository, UserService userService) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userService = userService;
    }

    @Transactional
    public CartResponse getCartByUser(String email) {
        User user = userService.findByEmail(email);
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> createCartForUser(user));
        return mapToResponse(cart);
    }

    @Transactional
    public CartResponse addItemToCart(String email, CartItemRequest request) {
        User user = userService.findByEmail(email);
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> createCartForUser(user));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        // Verificar si el producto ya existe en el carrito
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(request.productId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Actualizar cantidad
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.quantity());
        } else {
            // Agregar nuevo item
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(request.quantity());
            cart.addItem(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return mapToResponse(savedCart);
    }

    @Transactional
    public CartResponse updateCartItem(String email, Long itemId, Integer quantity) {
        User user = userService.findByEmail(email);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Carrito no encontrado"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Item no encontrado en el carrito"));

        if (quantity <= 0) {
            cart.removeItem(item);
        } else {
            item.setQuantity(quantity);
        }

        Cart savedCart = cartRepository.save(cart);
        return mapToResponse(savedCart);
    }

    @Transactional
    public CartResponse removeItemFromCart(String email, Long itemId) {
        User user = userService.findByEmail(email);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Carrito no encontrado"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Item no encontrado en el carrito"));

        cart.removeItem(item);
        Cart savedCart = cartRepository.save(cart);
        return mapToResponse(savedCart);
    }

    @Transactional
    public void clearCart(String email) {
        User user = userService.findByEmail(email);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Carrito no encontrado"));
        cart.clearItems();
        cartRepository.save(cart);
    }

    private Cart createCartForUser(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    private CartResponse mapToResponse(Cart cart) {
        return new CartResponse(
                cart.getId(),
                cart.getItems().stream()
                        .map(item -> new CartItemResponse(
                                item.getId(),
                                new ProductSummary(
                                        item.getProduct().getId(),
                                        item.getProduct().getName(),
                                        item.getProduct().getDescription(),
                                        item.getProduct().getPrice(),
                                        item.getProduct().getImageUrl()
                                ),
                                item.getQuantity()
                        ))
                        .toList()
        );
    }
}
