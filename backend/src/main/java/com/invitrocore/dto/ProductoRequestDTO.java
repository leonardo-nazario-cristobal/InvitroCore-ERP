package com.invitrocore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public class ProductoRequestDTO {

   private String codigoBarras;

   @NotBlank(message = "El nombre es obligatorio")
   @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
   private String nombre;

   private String descripcion;

   @NotNull(message = "El precio es obligatorio")
   @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
   private BigDecimal precio;

   @Min(value = 0, message = "El stock minimo no puede ser negativo")
   private Integer stockMinimo;

   private Long idCategoria;

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

   public Integer getStockMinimo() {
      return stockMinimo;
   }

   public Long getIdCategoria() {
      return idCategoria;
   }

   public void setCodigoBarras(String codigoBarras) {
      this.codigoBarras = codigoBarras;
   }

   public void setNombre(String nombre) {
      this.nombre = nombre;
   }

   public void setDescripcion(String descripcion) {
      this.descripcion = descripcion;
   }

   public void setPrecio(BigDecimal precio) {
      this.precio = precio;
   }

   public void setStockMinimo(Integer stockMinimo) {
      this.stockMinimo = stockMinimo;
   }

   public void setIdCategoria(Long idCategoria) {
      this.idCategoria = idCategoria;
   }
}
