package com.frtena.catalog.controller;

import com.frtena.catalog.pojo.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller; // Cambio de @RestController a @Controller
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller // Cambio de @RestController a @Controller
public class CatalogController {

    // URL de la API de la fuente de datos
    private final String apiUrl = "https://fakestoreapi.com/products";

    @GetMapping("/productos")
    public String obtenerCatalogo(Model model) {
        // URL de la API de la fuente de datos
        String apiUrl = "https://fakestoreapi.com/products";

        // Realizar una solicitud HTTP GET para obtener los datos de la API
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Product[]> response = restTemplate.getForEntity(apiUrl, Product[].class);

        // Extraer los productos de la respuesta
        Product[] products = response.getBody();

        // Convertir los productos en una lista
        List<Product> productList = Arrays.asList(products);

        // Agregar la lista de productos al modelo para pasarla a la vista
        model.addAttribute("productos", productList);

        // Devuelve el nombre de la vista "productos" (productos.html)
        return "productos";
    }

    @GetMapping("/productos-por-categoria")
    public String obtenerCatalogoPorCategoria(@RequestParam(value = "categoria", required = false) String categoria, Model model) {

        // Realizar una solicitud HTTP GET para obtener los datos de la API
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Product[]> response = restTemplate.getForEntity(apiUrl, Product[].class);

        // Extraer los productos de la respuesta
        Product[] products = response.getBody();

        // Convertir los productos en una lista
        List<Product> productList = Arrays.asList(products);

        // Filtrar los productos por la categoría especificada (si se proporciona)
        if (categoria != null && !categoria.isEmpty()) {
            productList = productList.stream()
                    .filter(product -> product.getCategory().equalsIgnoreCase(categoria))
                    .collect(Collectors.toList());
        }

        // Agregar la lista de productos al modelo para pasarla a la vista
        model.addAttribute("productos", productList);

        // Devuelve el nombre de la vista "productos" (productos.html)
        return "productos";
    }

    @GetMapping("/productos-por-precio")
    public String obtenerCatalogoOrdenadoPorPrecio(@RequestParam(value = "categoria", required = false) String categoria, Model model) {

        // Realizar una solicitud HTTP GET para obtener los datos de la API
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Product[]> response = restTemplate.getForEntity(apiUrl, Product[].class);

        // Extraer los productos de la respuesta
        Product[] products = response.getBody();

        // Convertir los productos en una lista
        List<Product> productList = Arrays.asList(products);

        // Ordenar la lista de productos por precio de menor a mayor
        productList.sort(Comparator.comparing(Product::getPrice));

        // Agregar la lista de productos ordenados al modelo y pasarla a la vista
        model.addAttribute("productos", productList);

        return "productos";
    }

    @GetMapping("/productos-por-rating")
    public String obtenerCatalogoOrdenadoPorRating(@RequestParam(value = "categoria", required = false) String categoria, Model model) {

        // Realizar una solicitud HTTP GET para obtener los datos de la API
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Product[]> response = restTemplate.getForEntity(apiUrl, Product[].class);

        // Extraer los productos de la respuesta
        Product[] products = response.getBody();

        // Convertir los productos en una lista
        List<Product> productList = Arrays.asList(products);

        // Ordenar la lista de productos por rating de mayor a menor
        //productList.sort(Comparator.comparing(Product::getRating).thenComparing(Rating::getRate)
        //        .reversed()); // Reversed para ordenar de mayor a menor

        // Agregar la lista de productos ordenados al modelo y pasarla a la vista
        model.addAttribute("productos", productList);

        return "productos";
    }

}
