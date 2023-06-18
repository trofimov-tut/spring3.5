package com.trofimov.sequrity.spring32.service;

import com.trofimov.sequrity.spring32.entity.User;
import java.util.List;

public interface UserService {

    public List<User> getAllUsers();

    public void deleteUser(Long id);

    public void updateUser(User user);

    public User getUserById(Long id);

    public User findUserByUsername(String username);

    public void saveUser(User user);

}
