package com.invitrocore.dto;

import com.invitrocore.model.TipoMovimiento;
import java.time.OffsetDateTime;

public record MovimientoInventarioResponseDTO(
      Long id,
      Long idProducto,
      String nombreProducto,
      String codigoBarras,
      TipoMovimiento tipo,
      Integer cantidad,
      String motivo,
      Long idUsuario,
      String nombreUsuario,
      OffsetDateTime creadoEn) {

}
