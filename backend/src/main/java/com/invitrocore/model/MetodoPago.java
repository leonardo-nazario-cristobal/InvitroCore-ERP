package com.invitrocore.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MetodoPago {

   EFECTIVO("efectivo"),
   TARJETA("tarjeta"),
   TRANSFERENCIA("transferencia");

   private final String valor;

   MetodoPago(String valor) {
      this.valor = valor;
   }

   @JsonValue
   public String getValor() {
      return valor;
   }

   @JsonCreator
   public static MetodoPago fromValor(String valor) {
      for (MetodoPago m : values()) {
         if (m.valor.equalsIgnoreCase(valor))
            return m;
      }
      throw new IllegalArgumentException("Método de pago inválido: " + valor);
   }
}
