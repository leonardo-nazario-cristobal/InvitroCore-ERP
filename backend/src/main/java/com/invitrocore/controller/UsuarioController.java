package com.invitrocore.controller;

import com.invitrocore.dto.UsuarioRequestDTO;
import com.invitrocore.dto.UsuarioResponseDTO;
import com.invitrocore.model.TipoRol;
import com.invitrocore.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

   private final UsuarioService usuarioService;

   public UsuarioController(UsuarioService usuarioService) {
      this.usuarioService = usuarioService;
   }

   /* POST /api/usuarios */

   @PostMapping
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<UsuarioResponseDTO> crearUsuario(
         @Valid @RequestBody UsuarioRequestDTO dto) {

      UsuarioResponseDTO response = usuarioService.crearUsuario(dto);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
   }

   /* GET /api/usuarios */

   @GetMapping
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
      return ResponseEntity.ok(usuarioService.listarUsuarios());
   }

   /* GET /api/usuarios/{id} */

   @GetMapping("/{id}")
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
      return ResponseEntity.ok(usuarioService.obtenerPorId(id));
   }

   /* PUT /api/usuarios/{id} */

   @PutMapping("/{id}")
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(
         @PathVariable Long id,
         @Valid @RequestBody UsuarioRequestDTO dto) {

      return ResponseEntity.ok(usuarioService.actualizarUsuario(id, dto));
   }

   /* PATCH /api/usuarios/{id}/desactivar */

   @PatchMapping("/{id}/desactivar")
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<Void> desactivarUsuario(@PathVariable Long id) {
      usuarioService.desactivarUsuario(id);
      return ResponseEntity.noContent().build();
   }

   /* PATCH /api/usuarios/{id}/activar */

   @PatchMapping("/{id}/activar")
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<Void> activarUsuario(@PathVariable Long id) {
      usuarioService.activarUsuario(id);
      return ResponseEntity.noContent().build();
   }

   /* PATCH /api/usuarios/{id}/rol */

   @PatchMapping("/{id}/rol")
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<Void> cambiarRol(
         @PathVariable Long id,
         @RequestParam TipoRol nuevoRol) {
      usuarioService.cambiarRol(id, nuevoRol);
      return ResponseEntity.noContent().build();
   }
}
