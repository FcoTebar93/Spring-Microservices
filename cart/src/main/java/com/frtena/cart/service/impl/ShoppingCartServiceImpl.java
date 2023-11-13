package com.frtena.cart.service.impl;

import com.frtena.cart.entity.ShoppingCart;
import com.frtena.cart.repository.ShoppingCartRepository;
import com.frtena.cart.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Override
    public List<ShoppingCart> getAllShoppingCarts() {
        return shoppingCartRepository.findAll();
    }

    @Override
    public ShoppingCart getShoppingCartById(long id) {
        return shoppingCartRepository.findById(id).orElse(null);
    }

    @Override
    public ShoppingCart saveShoppingCart(ShoppingCart shoppingCart) {
        return shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public void deleteShoppingCart(long id) {
        shoppingCartRepository.deleteById(id);
    }

    @Override
    public ShoppingCart findByUserId(long userId) {
        return shoppingCartRepository.findByUserId(userId);
    }
}

