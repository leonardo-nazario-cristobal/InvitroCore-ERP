package com.invitrocore.auth;

public record AuthResponseDTO(
      String accessToken,
      String refreshToken,
      String tipo) {

   public AuthResponseDTO(String accessToken, String refreshToken) {
      this(accessToken, refreshToken, "Bearer");
   }
}
