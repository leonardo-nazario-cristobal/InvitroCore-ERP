package com.invitrocore.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/* Entidad de la tabla usuarios */

@Entity
@Table(name = "usuarios")
public class Usuario {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false, length = 100)
   private String nombre;

   @Column(nullable = false, unique = true, length = 120)
   private String correo;

   @Column(name = "password", nullable = false)
   private String password;

   @Column(nullable = false)
   private TipoRol rol = TipoRol.CAJERO;

   @Column(nullable = false)
   private boolean activo = true;

   @Column(name = "creado_en", nullable = false, updatable = false)
   private OffsetDateTime creadoEn;

   @Column(name = "actualizado_en", nullable = false)
   private OffsetDateTime actualizadoEn;

   protected Usuario() {
   }

   public Usuario(String nombre, String correo, String password, TipoRol rol) {
      this.nombre = nombre;
      this.correo = correo;
      this.password = password;
      this.rol = rol;
      this.activo = true;
   }

   @PrePersist
   protected void onCreate() {
      this.creadoEn = OffsetDateTime.now();
      this.actualizadoEn = OffsetDateTime.now();
   }

   @PreUpdate
   protected void onUpdate() {
      this.actualizadoEn = OffsetDateTime.now();
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

   public String getPassword() {
      return password;
   }

   public TipoRol getRol() {
      return rol;
   }

   public boolean isActivo() {
      return activo;
   }

   public OffsetDateTime getCreadoEn() {
      return creadoEn;
   }

   public OffsetDateTime getActualizadoEn() {
      return actualizadoEn;
   }

   public void activar() {
      this.activo = true;
   }

   public void desactivar() {
      this.activo = false;
   }

   public void cambiarRol(TipoRol nuevoRol) {
      this.rol = nuevoRol;
   }

   public void actualizarNombre(String nuevoNombre) {
      this.nombre = nuevoNombre;
   }

   public void actualizarCorreo(String nuevoCorreo) {
      this.correo = nuevoCorreo;
   }

   public void cambiarPassword(String passwordHasheado) {
      this.password = passwordHasheado;
   }
}
