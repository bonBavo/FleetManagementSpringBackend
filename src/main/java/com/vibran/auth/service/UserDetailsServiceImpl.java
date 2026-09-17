package com.vibran.auth.service;


import com.vibran.domain.user.entity.User;
import com.vibran.domain.user.repositiory.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository
                .findByEmailAndIsActiveTrueAndIsDeletedFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found or inactive: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority(
                        "ROLE_" + user.getRole().name())))
                .accountExpired(false)
                .accountLocked(user.getSuspendedAt() != null)
                .credentialsExpired(false)
                .disabled(!user.getIsActive())
                .build();
    }
}