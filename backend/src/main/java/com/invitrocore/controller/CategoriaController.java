package com.invitrocore.controller;

import com.invitrocore.dto.CategoriaRequestDTO;
import com.invitrocore.dto.CategoriaResponseDTO;
import com.invitrocore.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

   private final CategoriaService categoriaService;

   public CategoriaController(CategoriaService categoriaService) {
      this.categoriaService = categoriaService;
   }

   /* POST /api/categorias */

   @PostMapping
   public ResponseEntity<CategoriaResponseDTO> crear(
         @Valid @RequestBody CategoriaRequestDTO dto) {

      return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.crear(dto));
   }

   /* GET /api/categorias */

   @GetMapping
   public ResponseEntity<List<CategoriaResponseDTO>> listar() {
      return ResponseEntity.ok(categoriaService.listar());
   }

   /* GET /api/categorias/{id} */

   @GetMapping("/{id}")
   public ResponseEntity<CategoriaResponseDTO> obtenerPorId(@PathVariable Long id) {
      return ResponseEntity.ok(categoriaService.obtenerPorId(id));
   }

   /* PUT /api/categorias/{id} */

   @PutMapping("/{id}")
   public ResponseEntity<CategoriaResponseDTO> actualizar(
         @PathVariable Long id,
         @Valid @RequestBody CategoriaRequestDTO dto) {

      return ResponseEntity.ok(categoriaService.actualizar(id, dto));
   }

   /* DELETE /api/categorias/{id} */

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> eliminar(@PathVariable Long id) {
      categoriaService.eliminar(id);
      return ResponseEntity.noContent().build();
   }
}
