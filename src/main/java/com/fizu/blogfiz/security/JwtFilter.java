package com.fizu.blogfiz.security;

import com.fizu.blogfiz.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if(authorization == null || !authorization.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        try{
            String jwtToken = authorization.substring(7) ;
            String username = jwtService.exctractUsername(jwtToken);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null && username != null){

//                if(jwtToken(jwtService.isTokenValid()))
            }
        }catch (Exception e){

        }

    }
}
