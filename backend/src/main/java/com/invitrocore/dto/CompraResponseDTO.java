package com.invitrocore.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record CompraResponseDTO(
      Long id,
      OffsetDateTime fecha,
      Long idProveedor,
      String nombreProveedor,
      BigDecimal total,
      List<DetalleCompraResponseDTO> detalles) {

}
