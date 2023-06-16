package com.trofimov.sequrity.spring32.service;

import com.trofimov.sequrity.spring32.entity.Role;
import com.trofimov.sequrity.spring32.entity.User;
import com.trofimov.sequrity.spring32.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByName(username);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Пользователь " + username + " не найден");
        }
        return new org.springframework.security.core.userdetails.User(userOptional.get().getName(), userOptional.get().getPassword(),
                authorities(userOptional.get().getRoles()));
    }

    private Collection<? extends GrantedAuthority> authorities(Collection<Role> roles){
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
}
