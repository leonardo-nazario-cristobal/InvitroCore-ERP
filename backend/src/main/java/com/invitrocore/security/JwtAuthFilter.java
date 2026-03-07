package com.invitrocore.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

   private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);
   private final JwtService jwtService;
   private final UserDetailsService userDetailsService;

   public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
      this.jwtService = jwtService;
      this.userDetailsService = userDetailsService;
   }

   @Override
   protected void doFilterInternal(
         @NonNull HttpServletRequest request,
         @NonNull HttpServletResponse response,
         @NonNull FilterChain filterChain) throws ServletException, IOException {

      /* Leer el Header */

      final String authHeader = request.getHeader("Authorization");

      /* Si no hay token o no empieza con baerer deja pasar sin autenticar */

      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
         filterChain.doFilter(request, response);
         return;
      }

      /* Extrae el token quitando baerer */

      final String token = authHeader.substring(7);

      try {
         final String correo = jwtService.extraerCorreo(token);

         if (correo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(correo);

            if (jwtService.esValido(token, userDetails)) {
               UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                     userDetails,
                     null,
                     userDetails.getAuthorities());
               authToken.setDetails(
                     new WebAuthenticationDetailsSource().buildDetails(request));
               SecurityContextHolder.getContext().setAuthentication(authToken);
            }
         }
      } catch (Exception e) {
         // Token expirado, malformado o inválido → Spring devuelve 401 automáticamente
         log.warn("Error procesando token: {}", e.getMessage());
      }

      filterChain.doFilter(request, response);
   }
}
