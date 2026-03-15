package com.invitrocore.controller;

import com.invitrocore.dto.MovimientoInventarioRequestDTO;
import com.invitrocore.dto.MovimientoInventarioResponseDTO;
import com.invitrocore.model.TipoMovimiento;
import com.invitrocore.service.MovimientoInventarioService;
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
@RequestMapping("/api/movimientos")
public class MovimientoInventarioController {

   private final MovimientoInventarioService movimientoService;

   public MovimientoInventarioController(MovimientoInventarioService movimientoService) {
      this.movimientoService = movimientoService;
   }

   /* POST /api/movimientos */

   @PostMapping
   @PreAuthorize("hasAnyRole('ADMIN', 'COMPRAS')")
   public ResponseEntity<MovimientoInventarioResponseDTO> registrar(
         @Valid @RequestBody MovimientoInventarioRequestDTO dto,
         @AuthenticationPrincipal UserDetails userDetails) {
      return ResponseEntity.status(HttpStatus.CREATED)
            .body(movimientoService.registrar(dto, userDetails.getUsername()));
   }

   /* GET /api/movimientos */

   @GetMapping
   @PreAuthorize("hasAnyRole('ADMIN', 'CCMPRAS')")
   public ResponseEntity<List<MovimientoInventarioResponseDTO>> listar() {
      return ResponseEntity.ok(movimientoService.listar());
   }

   /* GET /api/movimientos/{id} */

   @GetMapping("/{id}")
   @PreAuthorize("hasAnyRole('ADMIN', 'COMPRAS')")
   public ResponseEntity<MovimientoInventarioResponseDTO> obtenerPorId(
         @PathVariable Long id) {
      return ResponseEntity.ok(movimientoService.obtenerPorId(id));
   }

   /* GET /api/movimientos/producto/{idProducto} */

   @GetMapping("/producto/{idProducto}")
   @PreAuthorize("hasAnyRole('ADMIN', 'COMPRAS')")
   public ResponseEntity<List<MovimientoInventarioResponseDTO>> listarPorProducto(
         @PathVariable Long idProducto) {
      return ResponseEntity.ok(movimientoService.listarPorProducto(idProducto));
   }

   /* GET /api/movimientos/tipo?valor=entrada */

   @GetMapping("/tipo")
   @PreAuthorize("hasAnyRole('ADMIN', 'COMPRAS')")
   public ResponseEntity<List<MovimientoInventarioResponseDTO>> listarPorTipo(
         @RequestParam TipoMovimiento valor) {
      return ResponseEntity.ok(movimientoService.listarPorTipo(valor));
   }

   /* GET /api/usuario/{idUsuario} */

   @GetMapping("/usuario/{idUsuario}")
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<List<MovimientoInventarioResponseDTO>> listarPorUsuario(
         @PathVariable Long idUsuario) {
      return ResponseEntity.ok(movimientoService.listarPorUsuario(idUsuario));
   }

   /* GET /api/movimientos/reporte?desde= &hasta= */

   @GetMapping("/reporte")
   @PreAuthorize("hasAnyRole('ADMIN', 'COMPRAS')")
   public ResponseEntity<List<MovimientoInventarioResponseDTO>> listarPorRangoFecha(
         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {
      return ResponseEntity.ok(movimientoService.listarPorRangoFecha(desde, hasta));
   }
}
