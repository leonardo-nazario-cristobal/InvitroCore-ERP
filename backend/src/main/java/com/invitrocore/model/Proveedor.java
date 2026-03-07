package com.invitrocore.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "proveedores")
public class Proveedor {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false, length = 150)
   private String nombre;

   @Column(length = 30)
   private String telefono;

   @Column(length = 120)
   private String correo;

   @Column(name = "creado_en", nullable = false, updatable = false)
   private OffsetDateTime creadoEn;

   @Column(name = "actualizado_en", nullable = false)
   private OffsetDateTime actualizadoEn;

   protected Proveedor() {
   }

   public Proveedor(String nombre, String telefono, String correo) {
      this.nombre = nombre;
      this.telefono = telefono;
      this.correo = correo;
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

   public String getTelefono() {
      return telefono;
   }

   public String getCorreo() {
      return correo;
   }

   public OffsetDateTime getCreadoEn() {
      return creadoEn;
   }

   public OffsetDateTime getActualizadoEn() {
      return actualizadoEn;
   }

   public void actualizarNombre(String nuevoNombre) {
      this.nombre = nuevoNombre;
   }

   public void actualizarTelefono(String nuevoTelefono) {
      this.telefono = nuevoTelefono;
   }

   public void actualizarCorreo(String nuevoCorreo) {
      this.correo = nuevoCorreo;
   }
}
