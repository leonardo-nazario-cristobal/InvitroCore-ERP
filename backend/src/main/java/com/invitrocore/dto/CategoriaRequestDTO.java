package com.invitrocore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoriaRequestDTO {

   @NotBlank(message = "El Nombre Es Obligatorio")
   @Size(max = 100, message = "El Nombre No Puede Superar 100 Caracteres")
   private String nombre;

   @Size(max = 500, message = "La Desscripción No Puede Superar Los 500 Caracteres")
   private String descripcion;

   public String getNombre() {
      return nombre;
   }

   public String getDescripcion() {
      return descripcion;
   }

   public void setNombre(String nombre) {
      this.nombre = nombre;
   }

   public void setDescripcion(String descripcion) {
      this.descripcion = descripcion;
   }
}
