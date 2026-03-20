package com.invitrocore.controller;

import com.invitrocore.dto.CompraRequestDTO;
import com.invitrocore.dto.CompraResponseDTO;
import com.invitrocore.service.CompraService;
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
@RequestMapping("/api/compras")
public class CompraController {

   private final CompraService compraService;

   public CompraController(CompraService compraService) {
      this.compraService = compraService;
   }

   /* POST /api/compras */
   @PostMapping
   @PreAuthorize("hasAnyRole('ADMIN', 'COMPRAS')")
   public ResponseEntity<CompraResponseDTO> registrar(
         @Valid @RequestBody CompraRequestDTO dto,
         @AuthenticationPrincipal UserDetails userDetails) {
      return ResponseEntity.status(HttpStatus.CREATED)
            .body(compraService.registrar(dto, userDetails.getUsername()));
   }

   /* GET /api/compras */

   @GetMapping
   @PreAuthorize("hasAnyRole('ADMIN', 'COMPRAS')")
   public ResponseEntity<List<CompraResponseDTO>> listar() {
      return ResponseEntity.ok(compraService.listar());
   }

   /* GET /api/compras{id} */

   @GetMapping("/{id}")
   @PreAuthorize("hasAnyRole('ADMIN', 'COMPRAS')")
   public ResponseEntity<CompraResponseDTO> obtenerPorId(@PathVariable Long id) {
      return ResponseEntity.ok(compraService.obtenerPorId(id));
   }

   /* GET /api/compras/proveedor/{idProveedor} */

   @GetMapping("/proveedor/{idProveedor}")
   @PreAuthorize("hasAnyRole('ADMIN', 'COMPRAS')")
   public ResponseEntity<List<CompraResponseDTO>> listarPorProveedor(
         @PathVariable Long idProveedor) {

      return ResponseEntity.ok(compraService.listarPorProveedor(idProveedor));
   }

   /*
    * GET
    * /api/compras/reporte?desde=2026-01-01T00:00:00Z&hasta=2026-03-31T23:59:59Z
    */

   @GetMapping("/reporte")
   @PreAuthorize("hasAnyRole('ADMIN', 'COMPRAS')")
   public ResponseEntity<List<CompraResponseDTO>> listarPorRangoFecha(
         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {
      return ResponseEntity.ok(compraService.listarPorRangoFecha(desde, hasta));
   }
}
