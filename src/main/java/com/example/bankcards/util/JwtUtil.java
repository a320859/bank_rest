package com.example.bankcards.util;

import com.example.bankcards.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    private String key = "ddddddddddddddddddddddddaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaadddddddddddd";

    public String generateToken(User user) {
        Map<String, Object> claim = new HashMap<>();
        //claim.put("role", user.getRole().name());
        return Jwts.builder()
                .setSubject(user.getUsername())
               // .setClaims(claim)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }
}
