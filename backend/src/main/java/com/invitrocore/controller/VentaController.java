package com.invitrocore.controller;

import com.invitrocore.dto.VentaRequestDTO;
import com.invitrocore.dto.VentaResponseDTO;
import com.invitrocore.model.EstadoVenta;
import com.invitrocore.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

   private final VentaService ventaService;

   public VentaController(VentaService ventaService) {
      this.ventaService = ventaService;
   }

   /* POST /api/ventas */

   @PostMapping
   @PreAuthorize("hasAnyRole('ADMIN', 'CAJERO', 'VENTAS')")
   public ResponseEntity<VentaResponseDTO> registrar(
         @Valid @RequestBody VentaRequestDTO dto,
         @AuthenticationPrincipal UserDetails userDetails) {
      return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.registrar(dto, userDetails.getUsername()));
   }

   /* GET /api/ventas */

   @GetMapping
   @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
   public ResponseEntity<List<VentaResponseDTO>> listar() {
      return ResponseEntity.ok(ventaService.listar());
   }

   /* GET /api/ventas/{id} */

   @GetMapping("/{id}")
   @PreAuthorize("hasAnyRole('ADMIN', 'CAJERO', 'VENTAS')")
   public ResponseEntity<VentaResponseDTO> obtenerPorId(@PathVariable Long id) {
      return ResponseEntity.ok(ventaService.obtenerPorId(id));
   }

   /* GET /api/ventas/usuario/{idUsuario} */

   @GetMapping("/usuario/{idUsuario}")
   @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
   public ResponseEntity<List<VentaResponseDTO>> listarPorUsuario(
         @PathVariable Long idUsuario) {
      return ResponseEntity.ok(ventaService.listarPorUsuario(idUsuario));
   }

   /* GET /api/ventas/estado?valor=completada */

   @GetMapping("/estado")
   @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
   public ResponseEntity<List<VentaResponseDTO>> listarPorEstado(
         @RequestParam EstadoVenta valor) {
      return ResponseEntity.ok(ventaService.listarPorEstado(valor));
   }

   /* GET /api/ventas/reporte?desde= &hasta= */

   @GetMapping("/reporte")
   @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
   public ResponseEntity<List<VentaResponseDTO>> listarPorRangoFecha(
         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {
      return ResponseEntity.ok(ventaService.listarPorRangoFecha(desde, hasta));
   }

   /* PATCH /api/ventas/{id}/cancelar */
   @PatchMapping("/{id}/cancelar")
   @PreAuthorize("hasAnyRole('ADMIN', 'VENTAS')")
   public ResponseEntity<VentaResponseDTO> cancelar(
         @PathVariable Long id,
         @AuthenticationPrincipal UserDetails userDetails) {
      return ResponseEntity.ok(ventaService.cancelar(id, userDetails.getUsername()));
   }
}
