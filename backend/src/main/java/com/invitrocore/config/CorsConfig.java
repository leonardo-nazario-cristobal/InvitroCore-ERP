package com.invitrocore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

   @Value("${app.cors.allowed-origin}")
   private String allowebOrigin;

   @Bean
   public CorsConfigurationSource corsConfigurationSource() {

      CorsConfiguration config = new CorsConfiguration();
      config.setAllowedOrigins(List.of(allowebOrigin));
      config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
      config.setAllowedHeaders(List.of("*"));
      config.setExposedHeaders(List.of("Authorization"));
      config.setAllowCredentials(true);

      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", config);

      return source;
   }
}
