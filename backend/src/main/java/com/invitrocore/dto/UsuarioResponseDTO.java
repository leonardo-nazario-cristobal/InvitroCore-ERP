package com.invitrocore.dto;

import com.invitrocore.model.TipoRol;

public record UsuarioResponseDTO(
      Long id,
      String nombre,
      String correo,
      TipoRol rol,
      boolean activo

) {

   public UsuarioResponseDTO(Long id, String nombre, String correo, TipoRol rol, boolean activo) {
      this.id = id;
      this.nombre = nombre;
      this.correo = correo;
      this.rol = rol;
      this.activo = activo;
   }

   public Long getId() {
      return id;
   }

   public String getNombre() {
      return nombre;
   }

   public String getCorreo() {
      return correo;
   }

   public TipoRol getRol() {
      return rol;
   }

   public boolean isActivo() {
      return activo;
   }
}
