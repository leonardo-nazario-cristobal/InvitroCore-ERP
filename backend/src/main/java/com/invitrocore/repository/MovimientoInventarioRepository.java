package com.invitrocore.repository;

import com.invitrocore.model.MovimientoInventario;
import com.invitrocore.model.TipoMovimiento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

   List<MovimientoInventario> findByProductoIdOrderByCreadoEnDesc(Long idProducto);

   Page<MovimientoInventario> findByTipoOrderByCreadoEnDesc(TipoMovimiento tipo, Pageable pageable);

   List<MovimientoInventario> findByUsuarioIdOrderByCreadoEnDesc(Long idUsuario);

   List<MovimientoInventario> findByCreadoEnBetweenOrderByCreadoEnDesc(
         OffsetDateTime desde,
         OffsetDateTime hasta);

   List<MovimientoInventario> findByProductoIdAndCreadoEnBetweenOrderByCreadoEnDesc(
         Long idProducto,
         OffsetDateTime desde,
         OffsetDateTime hasta);

   @Query("""
               SELECT m FROM MovimientoInventario m
               JOIN FETCH m.producto
               LEFT JOIN FETCH m.usuario
               WHERE m.id = :id
         """)
   java.util.Optional<MovimientoInventario> findByIdWithDetails(@Param("id") Long id);

   @Query("""
               SELECT m FROM MovimientoInventario m
               JOIN FETCH m.producto
               LEFT JOIN FETCH m.usuario
               ORDER BY m.creadoEn DESC
         """)
   List<MovimientoInventario> findAllWithDetails();
}
