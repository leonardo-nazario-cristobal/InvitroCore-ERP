package com.invitrocore.converter;

import com.invitrocore.model.EstadoVenta;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EstadoVentaConverter implements AttributeConverter<EstadoVenta, String> {

   @Override
   public String convertToDatabaseColumn(EstadoVenta estadoVenta) {
      return estadoVenta != null ? estadoVenta.getValor() : null;
   }

   @Override
   public EstadoVenta convertToEntityAttribute(String valor) {
      if (valor == null)
         return null;
      return EstadoVenta.fromValor(valor);
   }
}
