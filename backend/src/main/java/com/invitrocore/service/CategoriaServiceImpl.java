package com.invitrocore.service;

import com.invitrocore.dto.CategoriaRequestDTO;
import com.invitrocore.dto.CategoriaResponseDTO;
import com.invitrocore.exception.BadRequestException;
import com.invitrocore.exception.ResourceNotFoundException;
import com.invitrocore.model.Categoria;
import com.invitrocore.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaServiceImpl implements CategoriaService {

   private final CategoriaRepository categoriaRepository;

   public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
      this.categoriaRepository = categoriaRepository;
   }

   /* Crear */

   @Override
   @Transactional
   public CategoriaResponseDTO crear(CategoriaRequestDTO dto) {

      if (categoriaRepository.existsByNombre(dto.getNombre())) {
         throw new BadRequestException("Ya Existe Una Categoria Con Ese Nombre");
      }

      Categoria categoria = new Categoria(dto.getNombre(), dto.getDescripcion());
      return toDTO(categoriaRepository.save(categoria));
   }

   /* Leer */

   @Override
   public CategoriaResponseDTO obtenerPorId(Long id) {
      return toDTO(buscarPorIdOFallar(id));
   }

   @Override
   public List<CategoriaResponseDTO> listar() {
      return categoriaRepository.findAll()
            .stream()
            .map(this::toDTO)
            .toList();
   }

   /* Actualizar */

   @Override
   @Transactional
   public CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO dto) {

      Categoria categoria = buscarPorIdOFallar(id);

      if (!categoria.getNombre().equalsIgnoreCase(dto.getNombre()) &&
            categoriaRepository.existsByNombre(dto.getNombre())) {
         throw new BadRequestException("Ya Existe Una Categoria Con Ese Nombre");
      }

      categoria.actualizarNombre(dto.getNombre());
      categoria.actualizarDescripcion(dto.getDescripcion());

      return toDTO(categoriaRepository.save(categoria));
   }

   /* Eliminar */

   @Override
   @Transactional
   public void eliminar(Long id) {
      if (!categoriaRepository.existsById(id)) {
         throw new ResourceNotFoundException("Categoria No Encontrada Con Id: " + id);
      }
      categoriaRepository.deleteById(id);
   }

   /* Helpers privados */

   private Categoria buscarPorIdOFallar(Long id) {
      return categoriaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                  "Categoria No Encontada Con Id: " + id));
   }

   private CategoriaResponseDTO toDTO(Categoria c) {

      return new CategoriaResponseDTO(
            c.getId(),
            c.getNombre(),
            c.getDescripcion());
   }
}
