package com.trofimov.sequrity.spring32.controllers;

import com.trofimov.sequrity.spring32.entity.Role;
import com.trofimov.sequrity.spring32.entity.User;
import com.trofimov.sequrity.spring32.service.RoleService;
import com.trofimov.sequrity.spring32.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RequestMapping("/api/admin")
@RestController
public class AdminRest {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminRest(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostMapping()
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            userService.saveUser(user);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(user.getId())
                    .toUri();
            return ResponseEntity.created(uri).body(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> editUser(@RequestBody User user) {
        try {
            userService.updateUser(user);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> showAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/role")
    public ResponseEntity<List<Role>> getRoleList() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}