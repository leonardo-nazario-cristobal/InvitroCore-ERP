package com.invitrocore.auth;

import com.invitrocore.model.TipoRol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequestDTO {

   @NotBlank(message = "El nombre es obligatorio")
   @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
   private String nombre;

   @NotBlank(message = "El correo es obligatorio")
   @Email(message = "El correo no tiene un formato válido")
   @Size(max = 120, message = "El correo no puede superar los 120 caracteres")
   private String correo;

   @NotBlank(message = "La contraseña es obligatoria")
   @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
   private String password;

   private TipoRol rol;

   public String getNombre() {
      return nombre;
   }

   public String getCorreo() {
      return correo;
   }

   public String getPassword() {
      return password;
   }

   public TipoRol getRol() {
      return rol;
   }

   public void setNombre(String nombre) {
      this.nombre = nombre;
   }

   public void setCorreo(String correo) {
      this.correo = correo;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public void setRol(TipoRol rol) {
      this.rol = rol;
   }
}
