package com.nobambidevteam.barberApi.modules.security.controller;

import com.nobambidevteam.barberApi.modules.security.dto.AuthLoginRequestDto;
import com.nobambidevteam.barberApi.modules.security.dto.AuthLoginResponseDto;
import com.nobambidevteam.barberApi.modules.security.service.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserDetailsServiceImpl userDetailsService;


    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponseDto> login(@RequestBody @Valid AuthLoginRequestDto request){

        return new ResponseEntity<>(this.userDetailsService.loginUser(request), HttpStatus.OK);

    }

    @GetMapping("/verify-role")
    @PreAuthorize("hasRole('ADMIN')")
    public String verifySecurityWithRole(){
        return "Tu token es válido y tienes el rol adecuado";
    }

    @GetMapping("/verify-permission")
    @PreAuthorize("hasAuthority('STAFF_MANAGE')")
    public String verifySecurityWithPermission(){
        return "Tu token es válido y tienes el permiso adecuado";
    }

}
