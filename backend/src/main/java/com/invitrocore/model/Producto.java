package com.invitrocore.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "productos")
public class Producto {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "codigo_barras", unique = true, length = 50)
   private String codigoBarras;

   @Column(nullable = false, unique = true, length = 150)
   private String nombre;

   @Column
   private String descripcion;

   @Column(nullable = false, precision = 10, scale = 2)
   private BigDecimal precio;

   @Column(name = "costo_promedio", precision = 10, scale = 2)
   private BigDecimal costoPromedio = BigDecimal.ZERO;

   @Column(nullable = false)
   private Integer stock = 0;

   @Column(name = "stock_minimo", nullable = false)
   private Integer stockMinimo = 5;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "id_categoria")
   private Categoria categoria;

   @Column(nullable = false)
   private boolean activo = true;

   @Column(name = "creado_en", nullable = false, updatable = false)
   private OffsetDateTime creadoEn;

   @Column(name = "actualizado_en", nullable = false)
   private OffsetDateTime actualizadoEn;

   protected Producto() {
   }

   public Producto(String codigoBarras, String nombre, String descripcion,
         BigDecimal precio, Integer stockMinimo, Categoria categoria) {
      this.codigoBarras = codigoBarras;
      this.nombre = nombre;
      this.descripcion = descripcion;
      this.precio = precio;
      this.stockMinimo = stockMinimo != null ? stockMinimo : 5;
      this.categoria = categoria;
      this.stock = 0;
      this.costoPromedio = BigDecimal.ZERO;
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

   public String getCodigoBarras() {
      return codigoBarras;
   }

   public String getNombre() {
      return nombre;
   }

   public String getDescripcion() {
      return descripcion;
   }

   public BigDecimal getPrecio() {
      return precio;
   }

   public BigDecimal getCostoPromedio() {
      return costoPromedio;
   }

   public Integer getStock() {
      return stock;
   }

   public Integer getStockMinimo() {
      return stockMinimo;
   }

   public Categoria getCategoria() {
      return categoria;
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

   public void actualizarNombre(String nuevoNombre) {
      this.nombre = nuevoNombre;
   }

   public void actualizarDescripcion(String nuevaDescripcion) {
      this.descripcion = nuevaDescripcion;
   }

   public void actualizarPrecio(BigDecimal nuevoPrecio) {
      this.precio = nuevoPrecio;
   }

   public void actualizarStockMinimo(Integer nuevoStockMinimo) {
      this.stockMinimo = nuevoStockMinimo;
   }

   public void actualizarCategoria(Categoria nuevaCategoria) {
      this.categoria = nuevaCategoria;
   }

   public void actualizarCodigoBarras(String nuevoCodigo) {
      this.codigoBarras = nuevoCodigo;
   }

   public void activar() {
      this.activo = true;
   }

   public void desactivar() {
      this.activo = false;
   }

   public void agregarStock(int cantidad) {
      this.stock += cantidad;
   }

   public void reducirStock(int cantidad) {
      if (cantidad > this.stock) {
         throw new IllegalArgumentException(
               "Stock insuficiente. Disponible: " + this.stock + ", solicitado: " + cantidad);
      }
      this.stock -= cantidad;
   }

   public void actualizarCostoPromedio(BigDecimal nuevoCosto) {
      this.costoPromedio = nuevoCosto;
   }

   public boolean tieneStockBajo() {
      return this.stock <= this.stockMinimo;
   }
}
