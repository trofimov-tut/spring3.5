package com.trofimov.sequrity.spring32.controllers;

import com.trofimov.sequrity.spring32.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminControllers {

    private final UserService userService;

    public AdminControllers(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/admin")
    public String allUsers(ModelMap model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }

}
