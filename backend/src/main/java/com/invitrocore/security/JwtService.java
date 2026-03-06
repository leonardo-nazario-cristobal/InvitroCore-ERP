package com.invitrocore.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

   private final SecretKey secretKey;
   private final long expirationMs;
   private final long refreshExpirationMs;

   public JwtService(
         @Value("${app.jwt.secret}") String secret,
         @Value("${app.jwt.expiration-ms}") long expirationMs,
         @Value("${app.jwt.refresh-expiration-ms}") long refreshExpirationMs) {

      this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
      this.expirationMs = expirationMs;
      this.refreshExpirationMs = refreshExpirationMs;
   }

   /* Generar Token */

   public String generarAccessToken(UserDetails userDetails) {
      return buildToken(userDetails.getUsername(), expirationMs);
   }

   public String generarRefreshToken(UserDetails userDetails) {
      return buildToken(userDetails.getUsername(), refreshExpirationMs);
   }

   /* Validar */

   public boolean esValido(String token, UserDetails userDetails) {
      final String correo = extraerCorreo(token);
      return correo.equals(userDetails.getUsername()) && !estaExpirado(token);
   }

   /* Extraer datos */

   public String extraerCorreo(String token) {
      return extraerClaim(token, Claims::getSubject);
   }

   /* Helpers privados */

   private String buildToken(String subject, long expiration) {
      return Jwts.builder()
            .subject(subject)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(secretKey)
            .compact();
   }

   private boolean estaExpirado(String token) {
      return extraerClaim(token, Claims::getExpiration).before(new Date());
   }

   private <T> T extraerClaim(String token, Function<Claims, T> resolver) {
      Claims claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
      return resolver.apply(claims);
   }
}