package com.invitrocore.repository;

import com.invitrocore.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

   boolean existsByNombre(String nombre);

   boolean existsByCodigoBarras(String codigoBarras);

   Optional<Producto> findByCodigoBarras(String codigoBarras);

   List<Producto> findByActivoTrue();

   List<Producto> findByActivoTrueAndCategoriaId(Long idCategoria);

   @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.stock <= p.stockMinimo")
   List<Producto> findProductosConStockBajo();

   List<Producto> findByActivoTrueAndNombreContainingIgnoreCase(String nombre);
}
