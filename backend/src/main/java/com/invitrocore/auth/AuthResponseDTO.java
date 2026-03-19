package com.invitrocore.auth;

public record AuthResponseDTO(
      String accessToken,
      String refreshToken,
      String tipo,
      String nombre,
      String correo,
      String rol) {

   public AuthResponseDTO(String accessToken, String refreshToken,
         String nombre, String correo, String rol) {
      this(accessToken, refreshToken, "Bearer", nombre, correo, rol);
   }
}
