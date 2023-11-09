package com.frtena.cart.controller;

import com.frtena.cart.entity.ShoppingCart;
import com.frtena.cart.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shoppingcart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/{id}")
    public String viewShoppingCart(@PathVariable long id, Model model) {
        ShoppingCart shoppingCart = shoppingCartService.getShoppingCartById(id);

        // Agrega el carrito al modelo para que puedas acceder a él en la vista
        model.addAttribute("shoppingCart", shoppingCart);

        // Retorna el nombre de la vista Thymeleaf
        return "shoppingCartDetails";
    }

    @PostMapping
    public ShoppingCart saveShoppingCart(@RequestBody ShoppingCart shoppingCart) {
        return shoppingCartService.saveShoppingCart(shoppingCart);
    }

    @DeleteMapping("/{id}")
    public void deleteShoppingCart(@PathVariable long id) {
        shoppingCartService.deleteShoppingCart(id);
    }
}

