package com.fizu.blogfiz.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private Long expiredToken;

    public String getToken(UserDetails userDetails){
        return buildToken(new HashMap<>(), userDetails);
    }

    private SecretKey getSignKey(){
        byte[] decode = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(decode);
    }

    public String buildToken(Map<String, Object> extraClaims, UserDetails userDetails){

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .signWith(getSignKey())
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+expiredToken))
                .compact();
    }

    public Claims getAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T getClaims(String token , Function<Claims ,T> claimsResolver){
        Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Date exctractExpired(String token){
        return getClaims(token, Claims::getExpiration);
    }

    public String exctractUsername(String token){
        return getClaims(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        return userDetails.getUsername().equals(exctractUsername(token));
    }

    public boolean isTokenExpired(String token){
        return exctractExpired(token).before(new Date());
    }


}
