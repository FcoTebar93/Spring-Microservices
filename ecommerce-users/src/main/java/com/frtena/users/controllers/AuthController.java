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
  private UserDetailsServiceImpl userDetailsService;

  @Autowired
  private PasswordEncoder encoder;

  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private PasswordEncoder passwordEncoder;

   @Autowired
   private RestTemplateBuilder restTemplateBuilder;



  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
                                            HttpSession session) {
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toList());

    // Agregar detalles del usuario a la sesión (puedes personalizar esto según tus necesidades)
    session.setAttribute("id", userDetails.getId());
    session.setAttribute("username", userDetails.getUsername());
    session.setAttribute("email", userDetails.getEmail());
    session.setAttribute("roles", roles);

    // Devolver el token en la respuesta
    Map<String, String> response = new HashMap<>();
    response.put("token", jwt);
    response.put("redirectUrl", "http://localhost:8098/productos");

    return ResponseEntity.ok(response);
  }

  @PostMapping("/register")
  public String registerUser(@ModelAttribute("signUpRequest") SignupRequest signUpRequest, Model model) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      model.addAttribute("error", "Username is already taken!");
      return "register"; // Nombre de la vista para mostrar el formulario de registro
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      model.addAttribute("error", "Email is already in use!");
      return "register"; // Nombre de la vista para mostrar el formulario de registro
    }

    // Crear un nuevo usuario y su carrito asociado
    User newUser = new User();
    newUser.setUsername(signUpRequest.getUsername());
    newUser.setEmail(signUpRequest.getEmail());
    newUser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

    // Asignar el rol por defecto (ROLE_USER) al nuevo usuario
    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    Set<Role> roles = new HashSet<>();
    roles.add(userRole);
    newUser.setRoles(roles);

    // Guardar el usuario en la base de datos
    newUser = userRepository.save(newUser);

    Long userId = newUser.getId();

    try {
      RestTemplate restTemplate = restTemplateBuilder.build();
      URI location = restTemplate.postForLocation("http://localhost:8095/shoppingcart/create-user-cart/{userId}", null, userId);

      if (location != null) {
        // Puedes hacer algo con la ubicación si es necesario
        System.out.println("New shopping cart created");
      } else {
        System.out.println("Shopping cart creation location is null.");
      }
    } catch (HttpClientErrorException e) {
      // Manejar la excepción según tus necesidades
      model.addAttribute("error", "Error creating user cart: " + e.getResponseBodyAsString());
      return "register";
    }

    model.addAttribute("success", "User registered successfully!");
    return "redirect:/login"; // Redirigir a la página de inicio de sesión después del registro exitoso
  }

  @GetMapping("/login")
  String login() {
    return "login";
  }

  @GetMapping("/register")
  public String register(Model model) {
    // Agregar un nuevo objeto User al modelo para el formulario
    model.addAttribute("signUpRequest", new SignupRequest());
    return "register";
  }
}
