package com.invitrocore.repository;

import com.invitrocore.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

   boolean existsByNombre(String nombre);

   Optional<Categoria> findByNombre(String nombre);

   Optional<Categoria> findByNombreIgnoreCase(String nombre);
}
