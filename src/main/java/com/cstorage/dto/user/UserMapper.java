package com.cstorage.dto.user;

import com.cstorage.dto.user.UserDTO;
import com.cstorage.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;


public class UserMapper {

    @Autowired
    PasswordEncoder passwordEncoder;
    public User toUser(UserDTO userDTO) {
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());
        user.setAuthority("USER_ROLE");
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEnabled(0);
        return user;
    }
//    public UserDTO toUserDTO(User user) {
//        UserDTO userDTO = new UserDTO();
//        userDTO.setEmail(user.getEmail());
//        userDTO.setUsername(user.getUsername());
//        userDTO.setAuthority(user.getAuthority());
//        userDTO.setEnabled(user.getEnabled());
//        return userDTO;
//    }
}
