package com.trofimov.sequrity.spring32.controllers;

import com.trofimov.sequrity.spring32.entity.User;
import com.trofimov.sequrity.spring32.service.RoleService;
import com.trofimov.sequrity.spring32.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.stream.Collectors;

@Controller
public class AdminControllers {

    private final UserService userService;
    private final RoleService roleService;

    public AdminControllers(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/")
    public String login() {
        return "redirect:/login";
    }

    @GetMapping("/admin")
    public String showAdminPage(Principal principal, Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("user", userService.findUserByUsername(principal.getName()));
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin/adminPage";
    }

    @GetMapping("/add")
    public String newUserPage(Principal principal, Model model) {
        model.addAttribute("user", userService.findUserByUsername(principal.getName()));
        model.addAttribute("roles", roleService.getAllRoles());
        return "admin/newUser";
    }

    @PostMapping("/new")
    public String createUser(@ModelAttribute("user") User user, @RequestParam(value = "checkedRoles") String[] result) {
        for (String s: result) {
            user.addRol(roleService.getRoleByName("ROLE_" + s));
        }
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @PostMapping("/{id}/update")
    public String updateUser(@ModelAttribute("user") User user, @PathVariable("id") Long id,
                             @RequestParam(value = "userRolesSelector") String[] result) {
        for (String s: result) {
            user.addRol(roleService.getRoleByName(s));
        }
        userService.updateUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

}
