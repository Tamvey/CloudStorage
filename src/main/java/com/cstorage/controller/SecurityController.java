package com.cstorage.controller;

import com.cstorage.dto.user.UserDTO;
import com.cstorage.dto.user.UserMapper;
import com.cstorage.entity.User;
import com.cstorage.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.UnsupportedEncodingException;


@Controller
public class SecurityController {

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();


    @GetMapping("/logout")
    public String logout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        logoutHandler.logout(request, response, authentication);
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerGet(Model model) {
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerPost(@ModelAttribute UserDTO userDTO, HttpServletRequest request) {
        User newUser = userMapper.toUser(userDTO);
        String siteURL = request.getRequestURL().toString();
        try {
            userService.register(newUser, siteURL.replace(request.getServletPath(), ""));
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/login";
    }

    @GetMapping("/verify")
    public String verifyUser(@Param("code") String code) throws Exception {
        if (userService.verify(code)) {
            return "redirect:/";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/login")
    public String loginGet() {
        return "login";
    }

}
