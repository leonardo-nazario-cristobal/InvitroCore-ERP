package com.invitrocore.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class DetalleCompraRequestDTO {

   @NotNull(message = "El id del producto es obligatorio")
   private Long idProducto;

   @NotNull(message = "La cantidad es obligatoria")
   @Min(value = 1, message = "La cantidad debe ser almenos 1")
   private Integer cantidad;

   @NotNull(message = "El costo unitario es obligatorio")
   @DecimalMin(value = "0.01", message = "El costo unitario debe ser mayor a 0")
   private BigDecimal costoUnitario;

   public Long getIdProducto() {
      return idProducto;
   }

   public Integer getCantidad() {
      return cantidad;
   }

   public BigDecimal getCostoUnitario() {
      return costoUnitario;
   }

   public void setIdProducto(Long idProducto) {
      this.idProducto = idProducto;
   }

   public void setCantidad(Integer cantidad) {
      this.cantidad = cantidad;
   }

   public void setCostoUnitario(BigDecimal costoUnitario) {
      this.costoUnitario = costoUnitario;
   }
}
