package com.invitrocore.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoMovimiento {

   ENTRADA("entrada"),
   SALIDA("salida"),
   AJUSTE("ajuste");

   private final String valor;

   TipoMovimiento(String valor) {
      this.valor = valor;
   }

   @JsonValue
   public String getValor() {
      return valor;
   }

   @JsonCreator
   public static TipoMovimiento fromValor(String valor) {
      for (TipoMovimiento t : values()) {
         if (t.valor.equalsIgnoreCase(valor))
            return t;
      }

      throw new IllegalArgumentException("Tipo de movimiento inválido: " + valor);
   }
}
