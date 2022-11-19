package com.wonjunkang.taskboard.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.wonjunkang.taskboard.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TokenProvider {
  @Value("${jwt_secret_key}")
  private String secretKey;

  public String createToken(User user) {
    // Valid for a day
    Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
    log.info("===> create a new token for eId: {}", user.getId());

    return Jwts.builder()//
        .signWith(SignatureAlgorithm.HS512, secretKey)//
        .claim("id", user.getId())//
        .setIssuer("TaskBoard")//
        .setIssuedAt(new Date())//
        .setExpiration(expiryDate)//
        .compact();
  }

  public String validateAndGetId(String token) {
    Claims claims = Jwts.parser()//
        .setSigningKey(secretKey)//
        .parseClaimsJws(token)//
        .getBody();

    return claims.get("id").toString();
  }


}


