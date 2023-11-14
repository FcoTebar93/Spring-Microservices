package com.frtena.cart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frtena.cart.controller.ShoppingCartController;
import com.frtena.cart.entity.CartItem;
import com.frtena.cart.entity.ShoppingCart;
import com.frtena.cart.service.impl.ShoppingCartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import static org.mockito.ArgumentMatchers.eq;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ShoppingCartControllerTest {

    @Mock
    private ShoppingCartServiceImpl shoppingCartService;

    @InjectMocks
    private ShoppingCartController shoppingCartController;

    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testViewShoppingCart() {
        long userId = 1L;
        ShoppingCart shoppingCart = new ShoppingCart();
        when(shoppingCartService.findByUserId(userId)).thenReturn(shoppingCart);

        String result = shoppingCartController.viewShoppingCart(userId, model, null);

        assertEquals("shoppingcart", result);
        verify(model).addAttribute("shoppingCart", shoppingCart);
    }

    @Test
    void testCreateUserCart() {
        // Configuración del entorno de prueba
        long userId = 1L;
        ShoppingCart shoppingCart = new ShoppingCart();

        // Configura el comportamiento esperado del servicio utilizando Mockito
        when(shoppingCartService.saveShoppingCart(any())).thenReturn(shoppingCart);

        // Ejecuta el método que estás probando
        String result = shoppingCartController.createUserCart(userId, model);

        // Verifica los resultados esperados
        assertEquals("cart created successfully!", result);

        // Verifica que se haya agregado el carrito de compras al modelo
        verify(model).addAttribute(eq("shoppingCart"), any(ShoppingCart.class));
    }

    @Test
    void testAddItemToShoppingCart() throws JsonProcessingException {
        long shoppingCartUserId = 1L;
        long itemId = 1L;
        String apiUrl = "https://fakestoreapi.com/products/" + itemId;
        String jsonResponse = "{}";
        RestTemplate restTemplate = mock(RestTemplate.class);
        when(restTemplate.getForObject(apiUrl, String.class)).thenReturn(jsonResponse);

        ObjectMapper objectMapper = mock(ObjectMapper.class);
        CartItem cartItem = new CartItem();
        when(objectMapper.readValue(jsonResponse, CartItem.class)).thenReturn(cartItem);

        ShoppingCart shoppingCart = new ShoppingCart();
        when(shoppingCartService.findByUserId(shoppingCartUserId)).thenReturn(shoppingCart);

        ResponseEntity<String> responseEntity = shoppingCartController.addItemToShoppingCart(shoppingCartUserId, itemId, model);

        //assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Producto agregado al carrito correctamente", responseEntity.getBody());
        verify(shoppingCartService).saveShoppingCart(shoppingCart);
    }

    @Test
    void testRemoveItemFromShoppingCart() {
        long shoppingCartId = 1L;
        long itemId = 1L;
        ShoppingCart shoppingCart = new ShoppingCart();

        // Inicializar la lista de items si es null
        if (shoppingCart.getItems() == null) {
            shoppingCart.setItems(new ArrayList<>());
        }

        CartItem cartItem = new CartItem();
        cartItem.setId(itemId);
        cartItem.setPrice(10.0);

        shoppingCart.getItems().add(cartItem);
        when(shoppingCartService.getShoppingCartById(shoppingCartId)).thenReturn(shoppingCart);

        String result = shoppingCartController.removeItemFromShoppingCart(shoppingCartId, itemId, model);

        assertEquals("redirect:/shoppingcart/" + shoppingCartId, result);
        verify(shoppingCartService).saveShoppingCart(shoppingCart);
    }

}
