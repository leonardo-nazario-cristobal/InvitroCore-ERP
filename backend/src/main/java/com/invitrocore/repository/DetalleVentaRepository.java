package com.invitrocore.repository;

import com.invitrocore.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

   boolean existsByVentaIdAndProductoId(Long idVenta, Long idProducto);

   List<DetalleVenta> findByProductoId(Long idProducto);
}
