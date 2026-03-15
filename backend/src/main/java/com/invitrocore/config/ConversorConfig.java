package com.invitrocore.config;

import com.invitrocore.model.EstadoVenta;
import com.invitrocore.model.MetodoPago;
import com.invitrocore.model.TipoMovimiento;
import com.invitrocore.model.TipoRol;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConversorConfig implements WebMvcConfigurer {

   @Override
   public void addFormatters(FormatterRegistry registry) {

      registry.addConverter(String.class, EstadoVenta.class,
            valor -> EstadoVenta.fromValor(valor.trim()));

      registry.addConverter(String.class, MetodoPago.class,
            valor -> MetodoPago.fromValor(valor.trim()));

      registry.addConverter(String.class, TipoRol.class,
            valor -> TipoRol.fromValor(valor.trim()));

      registry.addConverter(String.class, TipoMovimiento.class,
            valor -> TipoMovimiento.fromValor(valor.trim()));
   }
}
