package com.nobambidevteam.barberApi.modules.security.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.nobambidevteam.barberApi.modules.security.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Collection;

public class JwtTokenValidator extends OncePerRequestFilter {

    private JwtUtils jwtUtils;
    private final HandlerExceptionResolver resolver;

    public JwtTokenValidator(JwtUtils jwtUtils, HandlerExceptionResolver resolver) {
        this.jwtUtils = jwtUtils;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(jwtToken != null && jwtToken.startsWith("Bearer ")){
            try{
                jwtToken = jwtToken.substring(7);
                DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);

                String username = jwtUtils.extractUsername(decodedJWT);
                String authorities = jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString();

                Collection<? extends GrantedAuthority> authoritiesList =
                        AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);


                SecurityContext securityContext = SecurityContextHolder.getContext();
                Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authoritiesList);
                securityContext.setAuthentication(authentication);
                SecurityContextHolder.setContext(securityContext);

            } catch (JWTVerificationException ex) {

                // Capturamos la excepción de Auth0 y la enviamos a  GlobalExceptionHandler
                resolver.resolveException(request, response, null, ex);
                return; // return aquí para detener la petición y no continuar por los filtros
            }

        }

        filterChain.doFilter(request, response);
    }


}