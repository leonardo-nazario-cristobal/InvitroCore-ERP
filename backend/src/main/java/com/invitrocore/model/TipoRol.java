package com.invitrocore.model;

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

   public String getValor() {
      return valor;
   }
}
