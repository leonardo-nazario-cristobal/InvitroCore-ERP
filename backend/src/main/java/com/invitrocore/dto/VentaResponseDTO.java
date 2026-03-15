package com.invitrocore.dto;

import com.invitrocore.model.EstadoVenta;
import com.invitrocore.model.MetodoPago;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record VentaResponseDTO(
      Long id,
      OffsetDateTime fecha,
      Long idUsuario,
      String nombreUsuario,
      MetodoPago metodoPago,
      EstadoVenta estado,
      BigDecimal total,
      List<DetalleVentaResponseDTO> detalles) {
}
