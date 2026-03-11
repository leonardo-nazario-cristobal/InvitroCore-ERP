package com.invitrocore.dto;

import java.math.BigDecimal;

public record ProductoStockBajoDTO(
      Long id,
      String codigoBarras,
      String nombre,
      Integer stock,
      Integer stockMinimo,
      BigDecimal costoPromedio) {

}
