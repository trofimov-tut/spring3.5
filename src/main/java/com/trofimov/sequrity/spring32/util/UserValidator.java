package com.trofimov.sequrity.spring32.util;

import com.trofimov.sequrity.spring32.entity.User;
import com.trofimov.sequrity.spring32.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    private final UserService userService;

    @Autowired
    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        if (userService.findUserByUsername(user.getName()) != null) {
            errors.rejectValue("username", "", "Пользователь с таким именем уже существует");
        }
    }

}
