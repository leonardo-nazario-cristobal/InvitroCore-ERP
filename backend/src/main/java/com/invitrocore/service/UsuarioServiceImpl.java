package com.invitrocore.service;

import com.invitrocore.dto.UsuarioRequestDTO;
import com.invitrocore.dto.UsuarioResponseDTO;
import com.invitrocore.exception.BadRequestException;
import com.invitrocore.exception.ResourceNotFoundException;
import com.invitrocore.model.TipoRol;
import com.invitrocore.model.Usuario;
import com.invitrocore.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

   private final UsuarioRepository usuarioRepository;
   private final PasswordEncoder passwordEncoder;

   public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
         PasswordEncoder passwordEncoder) {
      this.usuarioRepository = usuarioRepository;
      this.passwordEncoder = passwordEncoder;
   }

   /* Crear */

   @Override
   @Transactional
   public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto) {

      if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
         throw new BadRequestException("El correo ya está registrado");
      }

      /* El rol es opcional en DTO si no viene se asigna Cajero */

      TipoRol rol = dto.getRol() != null ? dto.getRol() : TipoRol.CAJERO;

      Usuario usuario = new Usuario(
            dto.getNombre(),
            dto.getCorreo(),
            passwordEncoder.encode(dto.getPassword()),
            rol);

      return toDTO(usuarioRepository.save(usuario));
   }

   /* Leer */

   @Override
   public UsuarioResponseDTO obtenerPorId(Long id) {
      return toDTO(buscarPorIdOFallar(id));
   }

   @Override
   public List<UsuarioResponseDTO> listarUsuarios() {
      return usuarioRepository.findByActivoTrue()
            .stream()
            .map(this::toDTO)
            .toList();
   }

   /* Actualizar */

   @Override
   @Transactional
   public UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO dto) {

      Usuario usuario = buscarPorIdOFallar(id);

      /* Solo actualiza correo si cambio, y valida que no este en uso */

      if (!usuario.getCorreo().equals(dto.getCorreo()) &&
            usuarioRepository.existsByCorreo(dto.getCorreo())) {

         throw new BadRequestException("El correo ya Está en uso");
      }

      usuario.actualizarNombre(dto.getNombre());
      usuario.actualizarCorreo(dto.getCorreo());

      /* Solo cambia el password si viene en el request */

      if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
         usuario.cambiarPassword(passwordEncoder.encode(dto.getPassword()));
      }

      if (dto.getRol() != null) {
         usuario.cambiarRol(dto.getRol());
      }

      return toDTO(usuarioRepository.save(usuario));
   }

   /* Activar / Desactivar */

   @Override
   @Transactional
   public void desactivarUsuario(Long id) {
      Usuario usuario = buscarPorIdOFallar(id);
      usuario.desactivar();
      usuarioRepository.save(usuario);
   }

   @Override
   @Transactional
   public void activarUsuario(Long id) {
      Usuario usuario = buscarPorIdOFallar(id);
      usuario.activar();
      usuarioRepository.save(usuario);
   }

   /* Cambiar Rol */

   @Override
   @Transactional
   public void cambiarRol(Long id, TipoRol nuevoRol) {
      Usuario usuario = buscarPorIdOFallar(id);
      usuario.cambiarRol(nuevoRol);
      usuarioRepository.save(usuario);
   }

   /* Helpers Privados */

   private Usuario buscarPorIdOFallar(Long id) {
      return usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                  "Usuario no encontrado con id: " + id));
   }

   private UsuarioResponseDTO toDTO(Usuario u) {
      return new UsuarioResponseDTO(
            u.getId(),
            u.getNombre(),
            u.getCorreo(),
            u.getRol(),
            u.isActivo());
   }
}