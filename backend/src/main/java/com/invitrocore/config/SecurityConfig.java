package com.invitrocore.config;

import com.invitrocore.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class SecurityConfig {

   private final JwtAuthFilter jwtAuthFilter;
   private final UserDetailsService userDetailsService;
   private final PasswordEncoder passwordEncoder;
   private final CorsConfigurationSource corsConfigurationSource;

   public SecurityConfig(JwtAuthFilter jwtAuthFilter,
         UserDetailsService userDetailsService,
         PasswordEncoder passwordEncoder,
         CorsConfigurationSource corsConfigurationSource) {

      this.jwtAuthFilter = jwtAuthFilter;
      this.userDetailsService = userDetailsService;
      this.passwordEncoder = passwordEncoder;
      this.corsConfigurationSource = corsConfigurationSource;
   }

   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                  .requestMatchers("/api/auth/**").permitAll()
                  .anyRequest().authenticated())
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

      return http.build();
   }

   @Bean
   public AuthenticationProvider authenticationProvider() {
      DaoAuthenticationProvider provider = new DaoAuthenticationProvider(passwordEncoder);
      provider.setUserDetailsService(userDetailsService);
      return provider;
   }

   @Bean
   public AuthenticationManager authenticationManager(
         AuthenticationConfiguration config) throws Exception {
      return config.getAuthenticationManager();
   }
}