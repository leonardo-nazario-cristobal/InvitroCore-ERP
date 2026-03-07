package com.invitrocore.repository;

import com.invitrocore.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

   boolean existsByNombre(String nombre);

   List<Proveedor> findByNombreContainingIgnoreCase(String nombre);

}
