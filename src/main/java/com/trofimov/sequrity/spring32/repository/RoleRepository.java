package com.trofimov.sequrity.spring32.repository;

import com.trofimov.sequrity.spring32.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
