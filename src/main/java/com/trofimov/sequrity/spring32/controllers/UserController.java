package com.trofimov.sequrity.spring32.controllers;

import com.trofimov.sequrity.spring32.entity.User;
import com.trofimov.sequrity.spring32.service.UserService;
import com.trofimov.sequrity.spring32.util.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.security.Principal;

@Controller
public class UserController {

    private final UserService userService;

    private final UserValidator userValidator;

    @Autowired
    public UserController(UserService userService, UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }

    @GetMapping(value = "/")
    public String welcome() {
        return "redirect:/users/lk";
    }

    @GetMapping(value = "users/add")
    public String addUser(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "addUser";
    }

    @PostMapping(value = "users/add")
    public String addUser(@ModelAttribute("user") User user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        userService.add(user);
        return "redirect:/";
    }

    @GetMapping(value = "users/edit/{id}")
    public String editUser(ModelMap model, @PathVariable("id") int id) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "editUser";
    }

    @PostMapping(value = "users/edit/{id}")
    public String edit(@ModelAttribute("user") User user) {
        userService.editUser(user);
        return "redirect:/";
    }

    @GetMapping("users/delete/{id}")
    public String deleteUserById(@PathVariable("id") int id) {
        userService.deleteUser(id);
        return "redirect:/";
    }

    @GetMapping("users/lk")
    public String show(Principal principal, ModelMap modelMap) {
        modelMap.addAttribute("user", userService.findUserByUsername(principal.getName()));
        return "show";
    }

}
