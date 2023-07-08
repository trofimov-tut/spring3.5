package com.trofimov.sequrity.spring32.controllers;

import com.trofimov.sequrity.spring32.entity.User;
import com.trofimov.sequrity.spring32.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/user")
public class UserRest {

    private final UserService userService;

    @Autowired
    public UserRest(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<User> getUser(Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        if (userService.getUserById(user.getId()) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            return ResponseEntity.ok(user);
        }
    }
}
