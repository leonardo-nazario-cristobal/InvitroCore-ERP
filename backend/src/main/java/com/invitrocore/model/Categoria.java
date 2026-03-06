package com.invitrocore.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "categorias")
public class Categoria {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false, unique = true, length = 100)
   private String nombre;

   @Column
   private String descripcion;

   @Column(name = "creado_en", nullable = false, updatable = false)
   private OffsetDateTime creadoEn;

   @Column(name = "actualizado_en", nullable = false)
   private OffsetDateTime actualizadoEn;

   protected Categoria() {
   }

   public Categoria(String nombre, String descripcion) {
      this.nombre = nombre;
      this.descripcion = descripcion;
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

   public String getDescripcion() {
      return descripcion;
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

   public void actualizarDescripcion(String nuevaDescripcion) {
      this.descripcion = nuevaDescripcion;
   }
}
