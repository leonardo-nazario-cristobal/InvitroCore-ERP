package com.invitrocore.dto;

import java.math.BigDecimal;

public record DetalleCompraResponseDTO(
      Long idProducto,
      String nombreProducto,
      String codigoBarras,
      Integer cantidad,
      BigDecimal costoUnitario,
      BigDecimal subtotal) {

}
