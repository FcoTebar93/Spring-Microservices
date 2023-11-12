package com.frtena.users.security.services;

import com.frtena.users.models.ERole;
import com.frtena.users.models.Role;
import com.frtena.users.models.User;
import com.frtena.users.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.frtena.users.repository.UserRepository;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
  UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private RestTemplate restTemplate;  // o el cliente HTTP que estés usando

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserDetailsImpl.build(user);
    }

    public User registerNewUser(User registeringUser) {
          User newUser = new User();
          newUser.setUsername(registeringUser.getUsername());
          newUser.setEmail(registeringUser.getEmail());
          newUser.setPassword(passwordEncoder.encode(registeringUser.getPassword()));

          // Asignar el rol por defecto (ROLE_USER) al nuevo usuario
          Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                  .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          Set<Role> roles = new HashSet<>();
          roles.add(userRole);
          newUser.setRoles(roles);

          // Guardar el usuario en la base de datos
        newUser = userRepository.save(newUser);

        restTemplate.postForObject("http://localhost:8095/create-user-cart" + newUser.getId(), null, Void.class);

        return newUser;
    }
}
