package com.invitrocore.repository;

import com.invitrocore.model.EstadoVenta;
import com.invitrocore.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, Long> {

   List<Venta> findByUsuarioId(Long idUsuario);

   List<Venta> findByEstado(EstadoVenta estado);

   List<Venta> findByFechaBetween(OffsetDateTime desde, OffsetDateTime hasta);

   List<Venta> findByUsuarioIdAndFechaBetween(
         Long idUsuario,
         OffsetDateTime desde,
         OffsetDateTime hasta);

   @Query("""
               SELECT DISTINCT v FROM Venta v
               JOIN FETCH v.usuario
               JOIN FETCH v.detalles d
               JOIN FETCH d.producto
               WHERE v.id = :id
         """)
   Optional<Venta> findByWithDetalles(@Param("id") Long id);
}
