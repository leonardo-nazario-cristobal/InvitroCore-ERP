package com.invitrocore.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class DetalleVentaRequestDTO {

   @NotNull(message = "El id del producto es obligatorio")
   private Long idProducto;

   @NotNull(message = "La cantidad es obligatoria")
   @Min(value = 1, message = "La cantidad debe ser al menos 1")
   private Integer cantidad;

   @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a 0")
   private BigDecimal precioUnitario;

   public Long getIdProducto() {
      return idProducto;
   }

   public Integer getCantidad() {
      return cantidad;
   }

   public BigDecimal getPrecioUnitario() {
      return precioUnitario;
   }

   public void setIdProducto(Long idProducto) {
      this.idProducto = idProducto;
   }

   public void setCantidad(Integer cantidad) {
      this.cantidad = cantidad;
   }

   public void setPrecioUnitario(BigDecimal precioUnitario) {
      this.precioUnitario = precioUnitario;
   }
}
