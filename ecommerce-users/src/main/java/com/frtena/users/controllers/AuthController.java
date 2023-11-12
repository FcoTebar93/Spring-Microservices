package com.frtena.users.controllers;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.frtena.users.models.Role;
import com.frtena.users.models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.frtena.users.security.services.UserDetailsImpl;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @PostMapping("/login")
  public String authenticateUser(@Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
                                 HttpServletResponse response,
                                 Model model) {
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toList());

    // Agregar detalles del usuario al modelo
    model.addAttribute("userId", userDetails.getId());
    model.addAttribute("username", userDetails.getUsername());
    model.addAttribute("email", userDetails.getEmail());
    model.addAttribute("roles", roles);

    // Redirigir a la URL deseada después del login
    return "redirect:http://localhost:8098/productos";
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

    // Create new user's account
    User user = new User(signUpRequest.getUsername(),
            signUpRequest.getEmail(),
            encoder.encode(signUpRequest.getPassword()));

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    roles.add(userRole);

    user.setRoles(roles);
    userRepository.save(user);

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
