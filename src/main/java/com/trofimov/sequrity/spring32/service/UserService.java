package com.trofimov.sequrity.spring32.service;

import com.trofimov.sequrity.spring32.entity.User;

import java.util.List;

public interface UserService {

    public List<User> getAllUsers();

    public void deleteUser(int id);

    public void editUser(User user);

    public User getUserById(int id);

    public User findUserByUsername(String username);

    public void add(User user);

}
