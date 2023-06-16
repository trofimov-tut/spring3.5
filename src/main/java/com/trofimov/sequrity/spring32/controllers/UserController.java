package com.trofimov.sequrity.spring32.controllers;

import com.trofimov.sequrity.spring32.service.UserService;
import com.trofimov.sequrity.spring32.util.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "login")
    public String loginPage() {
        return "users/login";
    }

    @GetMapping("user")
    public String showUserInfo(Principal principal, Model model) {
        model.addAttribute("user", userService.findUserByUsername(principal.getName()));
        return "users/userPage";
    }

}
