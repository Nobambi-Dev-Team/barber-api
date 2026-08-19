package com.nobambidevteam.barberApi.modules.security.service;

import com.nobambidevteam.barberApi.modules.security.dto.AuthLoginRequestDto;
import com.nobambidevteam.barberApi.modules.security.dto.AuthLoginResponseDto;
import com.nobambidevteam.barberApi.modules.security.entity.UserEntity;
import com.nobambidevteam.barberApi.modules.security.repository.IUserRepository;
import com.nobambidevteam.barberApi.modules.security.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl  implements UserDetailsService {

    private final IUserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

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

    public AuthLoginResponseDto loginUser(AuthLoginRequestDto request) {

        // Recuperar nombre de usuario y contraseña
        String username = request.username();
        String password = request.password();

        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.createToken(authentication);

        return new AuthLoginResponseDto(username, "Login successfull", accessToken, true);
    }

    private Authentication authenticate(String username, String password) {

        UserDetails userDetails = this.loadUserByUsername(username);

        if(!passwordEncoder.matches(password, userDetails.getPassword())){
            throw new BadCredentialsException("Invalid username or password");
        }

        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }
}