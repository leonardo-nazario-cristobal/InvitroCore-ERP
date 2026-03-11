package com.invitrocore.dto;

import java.math.BigDecimal;

public record ProductoResponseDTO(
      Long id,
      String codigoBarras,
      String nombre,
      String descripcion,
      BigDecimal precio,
      BigDecimal costoPromedio,
      Integer stock,
      Integer stockMinimo,
      boolean activo,
      boolean stockBajo,
      Long idCategoria,
      String nombreCategoria) {

}
