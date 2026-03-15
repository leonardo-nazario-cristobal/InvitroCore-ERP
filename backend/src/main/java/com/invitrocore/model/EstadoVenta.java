package com.invitrocore.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EstadoVenta {

   COMPLETADA("completada"),
   CANCELADA("cancelada"),
   PENDIENTE("pendiente");

   private final String valor;

   EstadoVenta(String valor) {
      this.valor = valor;
   }

   @JsonValue
   public String getValor() {
      return valor;
   }

   @JsonCreator
   public static EstadoVenta fromValor(String valor) {
      for (EstadoVenta e : values()) {
         if (e.valor.equalsIgnoreCase(valor))
            return e;
      }

      throw new IllegalArgumentException("Estado de venta inválido: " + valor);
   }
}
