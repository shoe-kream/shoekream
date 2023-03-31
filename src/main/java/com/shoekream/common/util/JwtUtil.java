package com.shoekream.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

import static com.shoekream.common.util.constants.JwtConstants.USER_ROLE_KEY_JWT;

public class JwtUtil {


    public static String createToken(String email, String role, String secretKey, long tokenValidMillis) {

        Claims claims = Jwts.claims()
                .setSubject(email);

        claims.put(USER_ROLE_KEY_JWT, role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidMillis))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

    }


    public static String getUserEmail(String token, String secretKey) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public static boolean isExpired(String token, String secretKey) {
        Jws<Claims> claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token);


        return claims.getBody()
                .getExpiration()
                .before(new Date());
    }

    public static Claims extractClaims(String token, String secretKey) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public static String getRole(String token, String secretKey) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get(USER_ROLE_KEY_JWT, String.class);
    }
}
