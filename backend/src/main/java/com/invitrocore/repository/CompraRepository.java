package com.invitrocore.repository;

import com.invitrocore.model.Compra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {

   List<Compra> findByProveedorId(Long idProveedor);

   List<Compra> findByFechaBetween(OffsetDateTime desde, OffsetDateTime hasta);

   List<Compra> findByProveedorIdAndFechaBetween(
         Long idProveedor,
         OffsetDateTime desde,
         OffsetDateTime hasta);

   @Query("""
            SELECT DISTINCT c FROM Compra c
            JOIN FETCH c.proveedor
            JOIN FETCH c.detalles d
            JOIN FETCH d.producto
            WHERE c.id = :id
         """)
   java.util.Optional<Compra> findByIdWithDetalles(@Param("id") Long id);
}
