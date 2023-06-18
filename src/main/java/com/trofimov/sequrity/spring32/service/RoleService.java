package com.trofimov.sequrity.spring32.service;

import com.trofimov.sequrity.spring32.entity.Role;

import java.util.List;

public interface RoleService {

    List<Role> getAllRoles();

    Role getRole(String userRole);

}
