package com.invitrocore.controller;

import com.invitrocore.dto.ProductoRequestDTO;
import com.invitrocore.dto.ProductoResponseDTO;
import com.invitrocore.dto.ProductoStockBajoDTO;
import com.invitrocore.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

   private final ProductoService productoService;

   public ProductoController(ProductoService productoService) {
      this.productoService = productoService;
   }

   /* POST /api/productos */

   @PostMapping
   @PreAuthorize("hasAnyRole('ADMIN', 'COMPRAS')")
   public ResponseEntity<ProductoResponseDTO> crear(
         @Valid @RequestBody ProductoRequestDTO dto) {
      return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(dto));
   }

   /* GET /api/productos */

   @GetMapping
   @PreAuthorize("isAuthenticated()")
   public ResponseEntity<List<ProductoResponseDTO>> listar() {
      return ResponseEntity.ok(productoService.listar());
   }

   @GetMapping("/inactivos")
   @PreAuthorize("hasAnyRole('ADMIN', 'COMPRAS')")
   public ResponseEntity<List<ProductoResponseDTO>> listarInactivos() {
      return ResponseEntity.ok(productoService.listarInactivos());
   }

   @GetMapping("/todos")
   @PreAuthorize("hasAnyRole('ADMIN', 'COMPRAS')")
   public ResponseEntity<List<ProductoResponseDTO>> listarTodos() {
      return ResponseEntity.ok(productoService.listarTodos());
   }

   /* GET /api/productos/{id} */

   @GetMapping("/{id}")
   @PreAuthorize("isAuthenticated()")
   public ResponseEntity<ProductoResponseDTO> obtenerPorId(@PathVariable Long id) {
      return ResponseEntity.ok(productoService.obtenerPorId(id));
   }

   /* GET /api/productos/codigo/{codigoBarras} */

   @GetMapping("/codigo/{codigoBarras}")
   @PreAuthorize("isAuthenticated()")
   public ResponseEntity<ProductoResponseDTO> obtenerPorCodigo(
         @PathVariable String codigoBarras) {
      return ResponseEntity.ok(productoService.obtenerPorCodigoBarras(codigoBarras));
   }

   /* GET /api/producto/categoria/{idCategoria} */

   @GetMapping("/categoria/{idCategoria}")
   @PreAuthorize("isAuthenticated()")
   public ResponseEntity<List<ProductoResponseDTO>> listarPorCategoria(
         @PathVariable Long idCategoria) {
      return ResponseEntity.ok(productoService.listarPorCategoria(idCategoria));
   }

   /* GET /api/productos/buscar?nombre=laptp */

   @GetMapping("/buscar")
   @PreAuthorize("isAuthenticated()")
   public ResponseEntity<List<ProductoResponseDTO>> buscarPorNombre(
         @RequestParam String nombre) {
      return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
   }

   /* GET /api/productos/stock-bajo */

   @GetMapping("/stock-bajo")
   @PreAuthorize("hasAnyRole('ADMIN', 'COMPRAS')")
   public ResponseEntity<List<ProductoStockBajoDTO>> listarStockBajo() {
      return ResponseEntity.ok(productoService.listarStockBajo());
   }

   /* PUT /api/productos/{id} */

   @PutMapping("/{id}")
   @PreAuthorize("hasAnyRole('ADMIN', 'COMPRAS')")
   public ResponseEntity<ProductoResponseDTO> actualizar(
         @PathVariable Long id,
         @Valid @RequestBody ProductoRequestDTO dto) {
      return ResponseEntity.ok(productoService.actualizar(id, dto));
   }

   /* PATCH /api/productos/{id}/desactivar */

   @PatchMapping("/{id}/desactivar")
   @PreAuthorize("hasAnyRole('ADMIN', 'COMPRAS')")
   public ResponseEntity<Void> desactivar(@PathVariable Long id) {
      productoService.desactivar(id);
      return ResponseEntity.noContent().build();
   }

   /* PATCH /api/productos/{id}/activar */

   @PatchMapping("/{id}/activar")
   @PreAuthorize("hasAnyRole('ADMIN', 'COMPRAS')")
   public ResponseEntity<Void> activar(@PathVariable Long id) {
      productoService.activar(id);
      return ResponseEntity.noContent().build();
   }
}
