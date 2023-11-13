package com.frtena.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frtena.cart.entity.CartItem;
import com.frtena.cart.entity.ShoppingCart;
import com.frtena.cart.service.impl.ShoppingCartServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartServiceImpl shoppingCartService;

    @GetMapping("/{id}")
    public String viewShoppingCart(@PathVariable long id, Model model, HttpSession httpSession) {
        ShoppingCart shoppingCart = shoppingCartService.findByUserId(id);

        // Agrega el carrito al modelo para que puedas acceder a él en la vista
       model.addAttribute("shoppingCart", shoppingCart);

        // Retorna el nombre de la vista Thymeleaf
        return "shoppingcart";
    }

    @PostMapping("/create-user-cart/{userId}")
    public String createUserCart(@PathVariable long userId, Model model) {
        // Crea un nuevo carrito
        ShoppingCart shoppingCart = new ShoppingCart();
        List<CartItem> cartItems = new ArrayList<>();

        // Asigna el ID del usuario al carrito
        shoppingCart.setUserId(userId);
        shoppingCart.setTotalPrice(0);
        shoppingCart.setItems(cartItems);

        // Guarda el carrito en la base de datos
        shoppingCartService.saveShoppingCart(shoppingCart);

        // Redirecciona a la vista de detalles del carrito recién creado
        model.addAttribute("shoppingCart", shoppingCart);
        return "cart created successfully!";
    }

    @PostMapping("/{shoppingCartUserId}/add-item/{itemId}")
    public ResponseEntity<String> addItemToShoppingCart(@PathVariable long shoppingCartUserId, @PathVariable long itemId, Model model) {
        try {
            // Hacer la solicitud a la API para obtener los detalles del producto
            String apiUrl = "https://fakestoreapi.com/products/" + itemId;
            RestTemplate restTemplate = new RestTemplate();
            String jsonResponse = restTemplate.getForObject(apiUrl, String.class);

            // Deserializar la respuesta JSON en un objeto CartItem
            ObjectMapper objectMapper = new ObjectMapper();
            CartItem cartItem = objectMapper.readValue(jsonResponse, CartItem.class);

            // Obtener el carrito existente por su ID de usuario
            ShoppingCart shoppingCart = shoppingCartService.findByUserId(shoppingCartUserId);

            // Agregar el nuevo elemento al carrito
            cartItem.setShoppingCart(shoppingCart);
            shoppingCart.getItems().add(cartItem);

            // Actualizar el precio total del carrito (según la lógica de tu aplicación)
            shoppingCart.setTotalPrice(shoppingCart.getTotalPrice() + cartItem.getPrice());

            // Guardar el carrito actualizado
            shoppingCartService.saveShoppingCart(shoppingCart);

            return new ResponseEntity<>("Producto agregado al carrito correctamente", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al agregar el producto al carrito", HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

