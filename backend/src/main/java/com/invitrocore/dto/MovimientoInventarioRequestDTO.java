package com.invitrocore.dto;

import com.invitrocore.model.TipoMovimiento;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MovimientoInventarioRequestDTO {

   @NotNull(message = "El id del producto es obligatorio")
   private Long idProducto;

   @NotNull(message = "El tipo de movimiento es obligatorio")
   private TipoMovimiento tipo;

   @NotNull(message = "La cantidad es obligatoria")
   @Min(value = 1, message = "La cantidad debe ser al menos 1")
   private Integer cantidad;

   @NotBlank(message = "El motivo es obligatorio")
   @Size(max = 255, message = "El motivo no puede superar 255 caracteres")
   private String motivo;

   public Long getIdProducto() {
      return idProducto;
   }

   public TipoMovimiento getTipo() {
      return tipo;
   }

   public Integer getCantidad() {
      return cantidad;
   }

   public String getMotivo() {
      return motivo;
   }

   public void setIdProducto(Long idProducto) {
      this.idProducto = idProducto;
   }

   public void setTipo(TipoMovimiento tipo) {
      this.tipo = tipo;
   }

   public void setCantidad(Integer cantidad) {
      this.cantidad = cantidad;
   }

   public void setMotivo(String motivo) {
      this.motivo = motivo;
   }
}