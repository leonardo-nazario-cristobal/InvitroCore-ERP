package com.invitrocore.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/* Difinicion de los Roles */

public enum TipoRol {
   ADMIN("admin"),
   CAJERO("cajero"),
   COMPRAS("compras"),
   VENTAS("ventas");

   private final String valor;

   TipoRol(String valor) {
      this.valor = valor;
   }

   @JsonValue
   public String getValor() {
      return valor;
   }

   @JsonCreator
   public static TipoRol fromValor(String valor) {
      return Arrays.stream(TipoRol.values())
            .filter(r -> r.valor.equalsIgnoreCase(valor))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Rol Inválido: " + valor));
   }
}
