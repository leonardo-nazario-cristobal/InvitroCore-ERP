package com.invitrocore.dto;

import java.math.BigDecimal;

public record DetalleVentaResponseDTO(
      Long idProducto,
      String nombreProducto,
      String codigoBarras,
      Integer cantidad,
      BigDecimal precioUnitario,
      BigDecimal subtotal) {

}
