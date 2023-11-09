package com.frtena.cart.service.impl;

import com.frtena.cart.entity.CartItem;
import com.frtena.cart.repository.CartItemRepository;
import com.frtena.cart.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartItemServiceImpl implements CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public List<CartItem> getAllCartItems() {
        return cartItemRepository.findAll();
    }

    @Override
    public CartItem getCartItemById(long id) {
        return cartItemRepository.findById(id).orElse(null);
    }

    @Override
    public CartItem saveCartItem(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }

    @Override
    public void deleteCartItem(long id) {
        cartItemRepository.deleteById(id);
    }
}
