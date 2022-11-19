package com.wonjunkang.taskboard.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;
import com.wonjunkang.taskboard.security.JwtAuthenticationFilter;

@EnableWebSecurity
public class WebSecurityConfig {

  @Autowired
  private JwtAuthenticationFilter JwtAuthenticationFilter;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors().and().csrf().disable().httpBasic().disable();

    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();

    http.authorizeRequests()//
        .antMatchers("/api/user/register", "/api/user/login").permitAll()//
        .anyRequest().authenticated();

    // For each request, do CorsFilter, and then JwtAuthenticationFilter
    http.addFilterAfter(JwtAuthenticationFilter, CorsFilter.class);

    return http.build();
  }
}
