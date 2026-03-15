package com.invitrocore.converter;

import com.invitrocore.model.TipoMovimiento;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoMovimientoConverter implements AttributeConverter<TipoMovimiento, String> {

   @Override
   public String convertToDatabaseColumn(TipoMovimiento tipo) {
      return tipo != null ? tipo.getValor() : null;
   }

   @Override
   public TipoMovimiento convertToEntityAttribute(String valor) {
      if (valor == null)
         return null;
      return TipoMovimiento.fromValor(valor);
   }
}
