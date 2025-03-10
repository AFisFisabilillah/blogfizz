package com.fizu.blogfiz.security;

import com.fizu.blogfiz.model.entity.User;
import com.fizu.blogfiz.model.repository.UserRepository;
import com.fizu.blogfiz.service.JwtService;
import com.fizu.blogfiz.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        log.info("Masuk ke jwtFilter dengan token " +authorization);
        if(authorization == null || !authorization.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        try{
            String jwtToken = authorization.substring(7) ;
            String username = jwtService.exctractUsername(jwtToken);
            log.info(username);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null && username != null){
                UserDetails userDetails = userService.loadUserByUsername(username);
                log.info("user detai name : "+userDetails.getUsername());
                if(jwtService.isTokenValid(jwtToken, userDetails)){
                    log.info("token valid");
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }else{
                    response.setStatus(403);
                    response.setHeader("Content-Type", "application/json");
                    response.getWriter().write("{error:\" token is invalid \" }");
                }
            }

            filterChain.doFilter(request, response);
        }catch (Exception e){
            log.info("error : "+e.toString());
        }

    }
}
