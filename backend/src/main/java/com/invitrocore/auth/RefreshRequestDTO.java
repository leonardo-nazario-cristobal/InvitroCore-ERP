package com.invitrocore.auth;

import jakarta.validation.constraints.NotBlank;

public class RefreshRequestDTO {

   @NotBlank(message = "El Refresh Token Es Obligatorio")
   private String refreshToken;

   public String getRefreshToken() {
      return refreshToken;
   }

   public void setRefreshToken(String refreshToken) {
      this.refreshToken = refreshToken;
   }

}
