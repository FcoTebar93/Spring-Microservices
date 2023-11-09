package com.frtena.users.service;

import com.frtena.users.dto.UserDto;
import com.frtena.users.entity.User;


import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findByEmail(String email);

    List<UserDto> findAllUsers();
}
