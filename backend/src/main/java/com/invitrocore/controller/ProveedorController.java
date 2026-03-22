package com.invitrocore.controller;

import com.invitrocore.dto.ProveedorRequestDTO;
import com.invitrocore.dto.ProveedorResponseDTO;
import com.invitrocore.service.ProveedorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

   private final ProveedorService proveedorService;

   public ProveedorController(ProveedorService proveedorService) {
      this.proveedorService = proveedorService;
   }

   @PostMapping
   @PreAuthorize("hasAnyRole('ADMIN', 'COMPRAS')")
   public ResponseEntity<ProveedorResponseDTO> crear(
         @Valid @RequestBody ProveedorRequestDTO dto) {
      return ResponseEntity.status(HttpStatus.CREATED).body(proveedorService.crear(dto));
   }

   @GetMapping
   @PreAuthorize("isAuthenticated()")
   public ResponseEntity<Page<ProveedorResponseDTO>> listar(
         @RequestParam(defaultValue = "0") int pagina,
         @RequestParam(defaultValue = "15") int tamanio) {
      return ResponseEntity.ok(proveedorService.listar(pagina, tamanio));
   }

   @GetMapping("/{id}")
   @PreAuthorize("isAuthenticated()")
   public ResponseEntity<ProveedorResponseDTO> obtenerPorId(@PathVariable Long id) {
      return ResponseEntity.ok(proveedorService.obtenerPorId(id));
   }

   @PutMapping("/{id}")
   @PreAuthorize("hasAnyRole('ADMIN', 'COMPRAS')")
   public ResponseEntity<ProveedorResponseDTO> actualizar(
         @PathVariable Long id,
         @Valid @RequestBody ProveedorRequestDTO dto) {
      return ResponseEntity.ok(proveedorService.actualizar(id, dto));
   }

   @DeleteMapping("/{id}")
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<Void> eliminar(@PathVariable Long id) {
      proveedorService.eliminar(id);
      return ResponseEntity.noContent().build();
   }
}