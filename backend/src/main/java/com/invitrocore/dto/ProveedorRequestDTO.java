package com.invitrocore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProveedorRequestDTO {

   @NotBlank(message = "El nombre es obligatorio")
   @Size(max = 150, message = "  El nombre no puede superar 150 caracteres")
   private String nombre;

   @Size(max = 30, message = "EL teléfono no puede superar 30 caracteres")
   private String telefono;

   @Email(message = "El correo no tiene un formato válido")
   @Size(max = 120, message = "El correo no puede superar 120 caracteres")
   private String correo;

   public String getNombre() {
      return nombre;
   }

   public String getTelefono() {
      return telefono;
   }

   public String getCorreo() {
      return correo;
   }

   public void setNombre(String nombre) {
      this.nombre = nombre;
   }

   public void setTelefono(String telefono) {
      this.telefono = telefono;
   }

   public void setCorreo(String correo) {
      this.correo = correo;
   }
}
