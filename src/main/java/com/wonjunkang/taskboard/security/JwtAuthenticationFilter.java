package com.wonjunkang.taskboard.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private TokenProvider tokenProvider;

  @Autowired
  public JwtAuthenticationFilter(TokenProvider tokenProvider) {
    this.tokenProvider = tokenProvider;
  }

  private String parseBearerToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      String token = parseBearerToken(request);

      // Validate token
      if (token != null && !token.equalsIgnoreCase("null")) {
        String userId = tokenProvider.validateAndGetId(token);
        log.info("Authenticated id: " + userId);

        AbstractAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userId, null, AuthorityUtils.NO_AUTHORITIES);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
      }
    } catch (Exception e) {
      log.error("Failed token set user id in security context", e);
    }

    filterChain.doFilter(request, response);
  }

}
