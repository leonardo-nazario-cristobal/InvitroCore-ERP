package com.invitrocore.converter;

import com.invitrocore.model.MetodoPago;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MetodoPagoConverter implements AttributeConverter<MetodoPago, String> {

   @Override
   public String convertToDatabaseColumn(MetodoPago metodoPago) {
      return metodoPago != null ? metodoPago.getValor() : null;
   }

   @Override
   public MetodoPago convertToEntityAttribute(String valor) {
      if (valor == null)
         return null;
      return MetodoPago.fromValor(valor);
   }
}
