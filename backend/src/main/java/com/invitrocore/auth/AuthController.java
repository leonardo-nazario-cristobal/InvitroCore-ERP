package com.invitrocore.auth;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

   private final AuthService authService;

   public AuthController(AuthService authService) {
      this.authService = authService;
   }

   /* POST /api/auth/login */

   @PostMapping("/login")
   public ResponseEntity<AuthResponseDTO> login(
         @Valid @RequestBody LoginRequestDTO dto) {
      return ResponseEntity.ok(authService.login(dto));
   }

   /* POST /api/auth/register */

   @PostMapping("/register")
   public ResponseEntity<AuthResponseDTO> register(
         @Valid @RequestBody RegisterRequestDTO dto) {
      return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
   }

   /* POST /api/auth/refresh */

   public ResponseEntity<AuthResponseDTO> refresh(
         @Valid @RequestBody RefreshRequestDTO dto) {
      return ResponseEntity.ok(authService.refresh(dto));
   }

   /* POST /api/auth/logout */

   @PostMapping("/logout")
   public ResponseEntity<Void> logout(
         @Valid @RequestBody RefreshRequestDTO dto) {
      authService.logout(dto);
      return ResponseEntity.noContent().build();
   }
}
