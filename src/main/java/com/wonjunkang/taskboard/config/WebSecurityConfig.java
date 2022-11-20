package com.wonjunkang.taskboard.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.filter.CorsFilter;
import com.wonjunkang.taskboard.security.JwtAuthenticationFilter;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // http security builder
    http.cors().and()//
        .csrf().disable()// csrf는 현재 사용하지 않으므로 disabled
        .httpBasic().disable()// token을 사용하므로 basic 인증 disable
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()//
        .authorizeRequests()//
        .antMatchers("/api/user/login", "/api/user/register").permitAll() //
        .anyRequest().authenticated(); // 이외 경로는 모두 인증

    // 매 요청마다 CorsFilter 실행 후에 JWT authentication Filter를 실행
    http.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);
  }


}
