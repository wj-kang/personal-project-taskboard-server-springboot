package com.wonjunkang.taskboard.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenProvider {
  @Value("${jwt_secret_key}")
  private String SECRET_KEY;

  public String createToken(String userId) {
    // for 1day
    Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));

    return Jwts.builder()//
        .signWith(SignatureAlgorithm.HS512, SECRET_KEY)//
        .setSubject(userId)//
        .setIssuer("task-board")//
        .setIssuedAt(new Date())//
        .setExpiration(expiryDate)//
        .compact();
  }

  public String validateAndGetId(String token) {
    Claims claims = Jwts.parser()//
        .setSigningKey(SECRET_KEY)//
        .parseClaimsJws(token)//
        .getBody();

    return claims.getSubject();
  }


}


