package com.invitrocore.repository;

import com.invitrocore.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

   boolean existsByNombre(String nombre);

   boolean existsByCodigoBarras(String codigoBarras);

   Optional<Producto> findByCodigoBarras(String codigoBarras);

   Page<Producto> findByActivoTrue(Pageable pageable);

   Page<Producto> findByActivoTrueAndCategoriaId(Long idCategoria, Pageable pageable);

   @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.stock <= p.stockMinimo")
   List<Producto> findProductosConStockBajo();

   Page<Producto> findByActivoTrueAndNombreContainingIgnoreCase(String nombre, Pageable pageable);
}
