package com.invitrocore.dto;

import com.invitrocore.model.MetodoPago;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class VentaRequestDTO {

   @NotNull(message = "El metodo de pago es obligatorio")
   private MetodoPago metodoPago;

   @Valid
   @NotEmpty(message = "La venta debe tener al menod un detalle")
   private List<DetalleVentaRequestDTO> detalles;

   public MetodoPago getMetodoPago() {
      return metodoPago;
   }

   public List<DetalleVentaRequestDTO> getDetalles() {
      return detalles;
   }

   public void setMetodoPago(MetodoPago metodoPago) {
      this.metodoPago = metodoPago;
   }

   public void setDetalles(List<DetalleVentaRequestDTO> detalles) {
      this.detalles = detalles;
   }
}
