package com.invitrocore.auth;

import jakarta.validation.constraints.NotBlank;

public class RefreshRequestDTO {

   @NotBlank(message = "El refresh token es obligatorio")
   private String refreshToken;

   public String getRefreshToken() {
      return refreshToken;
   }

   public void setRefreshToken(String refreshToken) {
      this.refreshToken = refreshToken;
   }

}
