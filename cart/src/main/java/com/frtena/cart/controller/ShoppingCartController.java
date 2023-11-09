package com.frtena.cart.controller;

import com.frtena.cart.entity.CartItem;
import com.frtena.cart.entity.ShoppingCart;
import com.frtena.cart.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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

    @PostMapping("/{id}/add-item")
    public String addItemToShoppingCart(@PathVariable long id, @RequestBody CartItem cartItem, Model model) {
        // Obtener el carrito existente por su ID
        ShoppingCart shoppingCart = shoppingCartService.getShoppingCartById(id);

        // Agregar el nuevo elemento al carrito
        shoppingCart.getItems().add(cartItem);

        // Actualizar el precio total del carrito (puedes hacer esto según la lógica de tu aplicación)
        shoppingCart.setTotalPrice((shoppingCart.getTotalPrice() + cartItem.getPrice()));

        // Guardar el carrito actualizado
        shoppingCartService.saveShoppingCart(shoppingCart);

        // Redireccionar a la vista de detalles del carrito
        model.addAttribute("shoppingCart", shoppingCart);
        return "redirect:/shoppingcart/" + id;
    }

    @DeleteMapping("/{id}/remove-item/{itemId}")
    public String removeItemFromShoppingCart(@PathVariable long id, @PathVariable long itemId, Model model) {
        // Obtener el carrito existente por su ID
        ShoppingCart shoppingCart = shoppingCartService.getShoppingCartById(id);

        // Buscar y eliminar el elemento del carrito por su ID
        Optional<CartItem> itemToRemove = shoppingCart.getItems().stream()
                .filter(item -> item.getId() == itemId)
                .findFirst();

        if (itemToRemove.isPresent()) {
            CartItem removedItem = itemToRemove.get();
            shoppingCart.getItems().remove(removedItem);

            // Actualizar el precio total del carrito (puedes hacer esto según la lógica de tu aplicación)
            shoppingCart.setTotalPrice(shoppingCart.getTotalPrice() - removedItem.getPrice());

            // Guardar el carrito actualizado
            shoppingCartService.saveShoppingCart(shoppingCart);
        }

        // Redireccionar a la vista de detalles del carrito
        model.addAttribute("shoppingCart", shoppingCart);
        return "redirect:/shoppingcart/" + id;
    }

}

