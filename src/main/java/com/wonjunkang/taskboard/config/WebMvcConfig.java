package com.wonjunkang.taskboard.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // bean
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {
  private final long MAX_AGE_SECS = 3600;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")//
        .allowedOrigins("*")//
        // .allowedOrigins("http://localhost:3000", "https://taskboard.wonjunkang.com")//
        .allowedHeaders("*")//
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")//
        .allowCredentials(true).maxAge(MAX_AGE_SECS);
  }
}
