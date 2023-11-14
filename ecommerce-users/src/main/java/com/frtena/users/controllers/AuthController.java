package com.frtena.users.controllers;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import com.frtena.users.models.Role;
import com.frtena.users.models.User;
import com.frtena.users.security.services.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.frtena.users.models.ERole;
import com.frtena.users.payload.request.LoginRequest;
import com.frtena.users.payload.request.SignupRequest;
import com.frtena.users.payload.response.JwtResponse;
import com.frtena.users.payload.response.MessageResponse;
import com.frtena.users.repository.RoleRepository;
import com.frtena.users.repository.UserRepository;
import com.frtena.users.security.jwt.JwtUtils;
import com.frtena.users.security.services.UserDetailsServiceImpl;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class AuthController {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private UserDetailsImpl userDetails;

  @Autowired
  private PasswordEncoder encoder;

  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private PasswordEncoder passwordEncoder;

   @Autowired
   private RestTemplateBuilder restTemplateBuilder;

   @Autowired
   private RestTemplate restTemplate;

  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
                                            HttpSession session) {
    // Enviar la solicitud de autenticación al API Gateway
    String gatewayUrl = "http://localhost:8093/ecommerce-users/login"; // ajusta la URL según tu configuración
    ResponseEntity<String> response = restTemplate.postForEntity(gatewayUrl, loginRequest, String.class);

    if (response.getStatusCode().is2xxSuccessful()) {
      // Procesa la respuesta del API Gateway como lo hacías antes
      String jwtToken = response.getBody(); // ajusta esto según la respuesta real del API Gateway

      UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(loginRequest.getUsername()); // Implementa este método según tus necesidades

      // Agregar detalles del usuario a la sesión (puedes personalizar esto según tus necesidades)
      session.setAttribute("id", userDetails.getId());
      session.setAttribute("username", userDetails.getUsername());
      session.setAttribute("email", userDetails.getEmail());
      session.setAttribute("roles", userDetails.getAuthorities().stream()
              .map(item -> item.getAuthority())
              .collect(Collectors.toList()));

      // Devolver el token en la respuesta
      Map<String, String> responseMap = new HashMap<>();
      responseMap.put("token", jwtToken);
      responseMap.put("redirectUrl", "http://localhost:8098/productos");

      return ResponseEntity.ok(responseMap);
    } else {
      // Maneja el caso de error, si es necesario
      return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
  }

  @PostMapping("/register")
  public String registerUser(@ModelAttribute("signUpRequest") SignupRequest signUpRequest, Model model) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      model.addAttribute("error", "Username is already taken!");
      return "register";
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      model.addAttribute("error", "Email is already in use!");
      return "register";
    }

    User newUser = new User();
    newUser.setUsername(signUpRequest.getUsername());
    newUser.setEmail(signUpRequest.getEmail());
    newUser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    Set<Role> roles = new HashSet<>();
    roles.add(userRole);
    newUser.setRoles(roles);

    newUser = userRepository.save(newUser);

    Long userId = newUser.getId();

    try {
      RestTemplate restTemplate = restTemplateBuilder.build();
      URI location = restTemplate.postForLocation("lb://ecommerce-cart/shoppingcart/create-user-cart/{userId}", null, userId);

      if (location != null) {
        System.out.println("Nuevo carrito de compras creado");
      } else {
        System.out.println("La ubicación de creación del carrito de compras es nula.");
      }
    } catch (HttpClientErrorException e) {
      model.addAttribute("error", "Error creating user cart: " + e.getResponseBodyAsString());
      return "register";
    }

    model.addAttribute("success", "User registered successfully!");
    return "redirect:/login";
  }

  @GetMapping("/login")
  String login() {
    return "login";
  }

  @GetMapping("/register")
  public String register(Model model) {
    model.addAttribute("signUpRequest", new SignupRequest());
    return "register";
  }
}
