package com.invitrocore.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

   @NotBlank(message = "El Correo Es Obligatorio")
   @Email(message = "El Correo No Tiene Un Formato Válido")
   private String correo;

   @NotBlank(message = "La Contraseña Es Obligatoria")
   private String password;

   public String getCorreo() {
      return correo;
   }

   public String getPassword() {
      return password;
   }

   public void setCorreo(String correo) {
      this.correo = correo;
   }

   public void setPassword(String password) {
      this.password = password;
   }
}
