package com.invitrocore.repository;

import com.invitrocore.model.DetalleCompra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleCompraRepository extends JpaRepository<DetalleCompra, Long> {

   boolean existsByCompraIdAndProductoId(Long idCompra, Long idProducto);

   List<DetalleCompra> findByProductoId(Long idProducto);
}
