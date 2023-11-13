package com.frtena.cart.service;
import com.frtena.cart.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    List<ShoppingCart> getAllShoppingCarts();
    ShoppingCart getShoppingCartById(long id);
    ShoppingCart saveShoppingCart(ShoppingCart shoppingCart);
    void deleteShoppingCart(long id);
    ShoppingCart findByUserId(long userId);
}
