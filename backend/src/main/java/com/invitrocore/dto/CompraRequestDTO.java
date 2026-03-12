package com.invitrocore.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CompraRequestDTO {

   @NotNull(message = "El id del proveedor es obligatorio")
   private Long idProveedor;

   @Valid
   @NotEmpty(message = "La compra debe tener almenos un detalle")
   private List<DetalleCompraRequestDTO> detalles;

   public Long getIdProveedor() {
      return idProveedor;
   }

   public List<DetalleCompraRequestDTO> getDetalles() {
      return detalles;
   }

   public void setIdProvedor(Long idProveedor) {
      this.idProveedor = idProveedor;
   }

   public void setDetalles(List<DetalleCompraRequestDTO> detalles) {
      this.detalles = detalles;
   }
}
