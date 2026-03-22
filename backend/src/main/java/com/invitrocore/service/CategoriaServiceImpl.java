package com.invitrocore.service;

import com.invitrocore.dto.CategoriaRequestDTO;
import com.invitrocore.dto.CategoriaResponseDTO;
import com.invitrocore.exception.BadRequestException;
import com.invitrocore.exception.ResourceNotFoundException;
import com.invitrocore.model.Categoria;
import com.invitrocore.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
         throw new BadRequestException("Ya existe una categoria con ese nombre");
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
   public Page<CategoriaResponseDTO> listar(int pagina, int tamanio) {
      Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by("nombre").ascending());
      return categoriaRepository.findAll(pageable)
            .map(this::toDTO);
   }

   /* Actualizar */

   @Override
   @Transactional
   public CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO dto) {

      Categoria categoria = buscarPorIdOFallar(id);

      if (!categoria.getNombre().equalsIgnoreCase(dto.getNombre()) &&
            categoriaRepository.existsByNombre(dto.getNombre())) {
         throw new BadRequestException("Ya existe una categoria con ese nombre");
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
         throw new ResourceNotFoundException("Categoria no encontrada con id: " + id);
      }
      categoriaRepository.deleteById(id);
   }

   /* Helpers privados */

   private Categoria buscarPorIdOFallar(Long id) {
      return categoriaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                  "Categoria no encontada con id: " + id));
   }

   private CategoriaResponseDTO toDTO(Categoria c) {

      return new CategoriaResponseDTO(
            c.getId(),
            c.getNombre(),
            c.getDescripcion());
   }
}
