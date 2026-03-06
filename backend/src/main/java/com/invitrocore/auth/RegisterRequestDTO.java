package com.invitrocore.auth;

import com.invitrocore.model.TipoRol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequestDTO {

   @NotBlank(message = "El Nombre Es Obligatorio")
   @Size(max = 100, message = "El Nombre No Puede Superar 100 Caracteres")
   private String nombre;

   @NotBlank(message = "El Correo Es Obligatorio")
   @Email(message = "El Correo No Tiene Un Formato Válido")
   @Size(max = 120, message = "El Correo No Puede Superar Los 120 Caracteres")
   private String correo;

   @NotBlank(message = "La Contraseña Es Obligatoria")
   @Size(min = 8, message = "La Contraseña Debe Tener Al Menos 8 caracteres")
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
