package com.invitrocore.converter;

import com.invitrocore.model.TipoRol;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

/* Convertir el enumn a una columna */

@Converter(autoApply = true)
public class TipoRolConverter implements AttributeConverter<TipoRol, String> {

   @Override
   public String convertToDatabaseColumn(TipoRol rol) {
      return rol == null ? null : rol.getValor();
   }

   @Override
   public TipoRol convertToEntityAttribute(String valor) {
      if (valor == null)
         return null;
      return Arrays.stream(TipoRol.values())
            .filter(r -> r.getValor().equals(valor))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Rol desconocido: " + valor));
   }
}