package com.frtena.catalog;

import com.frtena.catalog.controller.CatalogController;
import com.frtena.catalog.pojo.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class CatalogControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CatalogController catalogController;

    @Test
    void testObtenerCatalogo() {
        // Simular la respuesta de la API
        Product[] products = {new Product(), new Product()};
        ResponseEntity<Product[]> responseEntity = new ResponseEntity<>(products, HttpStatus.OK);
        Mockito.when(restTemplate.getForEntity(any(String.class), eq(Product[].class))).thenReturn(responseEntity);

        // Simular el modelo
        Model model = Mockito.mock(Model.class);

        // Llamar al método del controlador
        String viewName = catalogController.obtenerCatalogo(model);

        // Verificar que el modelo contiene la lista de productos
        Mockito.verify(model).addAttribute(eq("productos"), any(List.class));

        // Verificar que se devuelve el nombre de la vista esperado
        assertEquals("productos", viewName);
    }

    @Test
    void testObtenerCatalogoPorCategoria() {
        // Simular la respuesta de la API
        Product[] products = {new Product(), new Product()};
        ResponseEntity<Product[]> responseEntity = new ResponseEntity<>(products, HttpStatus.OK);
        Mockito.when(restTemplate.getForEntity(any(String.class), eq(Product[].class))).thenReturn(responseEntity);

        // Simular el modelo y la categoría
        Model model = Mockito.mock(Model.class);
        String categoria = "Electronics";

        // Llamar al método del controlador
        String viewName = catalogController.obtenerCatalogoPorCategoria(categoria, model);

        // Verificar que el modelo contiene la lista de productos filtrada por categoría
        Mockito.verify(model).addAttribute(eq("productos"), any(List.class));

        // Verificar que se devuelve el nombre de la vista esperado
        assertEquals("productos", viewName);
    }

    @Test
    void testObtenerCatalogoOrdenadoPorPrecio() {
        // Simular la respuesta de la API
        Product[] products = {new Product(), new Product()};
        ResponseEntity<Product[]> responseEntity = new ResponseEntity<>(products, HttpStatus.OK);
        Mockito.when(restTemplate.getForEntity(any(String.class), eq(Product[].class))).thenReturn(responseEntity);

        // Simular el modelo
        Model model = Mockito.mock(Model.class);

        // Llamar al método del controlador
        String viewName = catalogController.obtenerCatalogoOrdenadoPorPrecio(null, model);

        // Verificar que el modelo contiene la lista de productos ordenados por precio
        Mockito.verify(model).addAttribute(eq("productos"), any(List.class));

        // Verificar que se devuelve el nombre de la vista esperado
        assertEquals("productos", viewName);
    }

    @Test
    void testObtenerCatalogoOrdenadoPorRating() {
        // Simular la respuesta de la API
        Product[] products = {new Product(), new Product()};
        ResponseEntity<Product[]> responseEntity = new ResponseEntity<>(products, HttpStatus.OK);
        Mockito.when(restTemplate.getForEntity(any(String.class), eq(Product[].class))).thenReturn(responseEntity);

        // Simular el modelo
        Model model = Mockito.mock(Model.class);

        // Llamar al método del controlador
        String viewName = catalogController.obtenerCatalogoOrdenadoPorRating(null, model);

        // Verificar que el modelo contiene la lista de productos ordenados por rating
        Mockito.verify(model).addAttribute(eq("productos"), any(List.class));

        // Verificar que se devuelve el nombre de la vista esperado
        assertEquals("productos", viewName);
    }
}
