package com.nobambidevteam.barberApi.modules.security.service;

import com.nobambidevteam.barberApi.modules.security.entity.UserEntity;
import com.nobambidevteam.barberApi.modules.security.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl  implements UserDetailsService {

    private final IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + "no fue encontrado"));

        // Creamos una lista para los permisos
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        // traer roles y convertirlos en SimpleGrandAuthority
        user.getRoles()
                .forEach(role ->
                        authorityList.add(
                                new SimpleGrantedAuthority("ROLE_" + role.getCode())
                        )
                );

        // traer permisos y convertirlos en SimpleGrandAuthority
        user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getCode())));


        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                user.isActive(),
                true,
                true,
                true,
                authorityList // la lista con los roles y permisos en formato SimpleGrantedAuthority
        );

    }
}