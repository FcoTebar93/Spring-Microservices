package com.frtena.cart.service;

import com.frtena.cart.entity.CartItem;

import java.util.List;

public interface CartItemService {
    List<CartItem> getAllCartItems();
    CartItem getCartItemById(long id);
    CartItem saveCartItem(CartItem cartItem);
    void deleteCartItem(long id);
}
