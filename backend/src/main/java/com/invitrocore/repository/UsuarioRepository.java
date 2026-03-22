package com.invitrocore.repository;

import com.invitrocore.model.TipoRol;
import com.invitrocore.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

   /* Login y validaciones */

   Optional<Usuario> findByCorreo(String correo);

   boolean existsByCorreo(String correo);

   /* Listar solo usuarios activos para el ADMIN */

   Page<Usuario> findByActivoTrue(Pageable pageable);

   /* Buscar usuario activo por correo */

   Optional<Usuario> findByCorreoAndActivoTrue(String correo);

   /* Filtrar por rol */

   List<Usuario> findByRol(TipoRol rol);
}
