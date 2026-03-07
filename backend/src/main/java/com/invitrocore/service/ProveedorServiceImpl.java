package com.invitrocore.service;

import com.invitrocore.dto.ProveedorRequestDTO;
import com.invitrocore.dto.ProveedorResponseDTO;
import com.invitrocore.exception.BadRequestException;
import com.invitrocore.exception.ResourceNotFoundException;
import com.invitrocore.model.Proveedor;
import com.invitrocore.repository.ProveedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProveedorServiceImpl implements ProveedorService {

   private final ProveedorRepository proveedorRepository;

   public ProveedorServiceImpl(ProveedorRepository proveedorRepository) {
      this.proveedorRepository = proveedorRepository;
   }

   @Override
   @Transactional
   public ProveedorResponseDTO crear(ProveedorRequestDTO dto) {
      if (proveedorRepository.existsByNombre(dto.getNombre())) {
         throw new BadRequestException("Ya existe un proveedor con ese nombre");
      }
      Proveedor proveedor = new Proveedor(dto.getNombre(), dto.getTelefono(), dto.getCorreo());
      return toDTO(proveedorRepository.save(proveedor));
   }

   @Override
   public ProveedorResponseDTO obtenerPorId(Long id) {
      return toDTO(buscarPorIdOFallar(id));
   }

   @Override
   public List<ProveedorResponseDTO> listar() {
      return proveedorRepository.findAll()
            .stream()
            .map(this::toDTO)
            .toList();
   }

   @Override
   @Transactional
   public ProveedorResponseDTO actualizar(Long id, ProveedorRequestDTO dto) {
      Proveedor proveedor = buscarPorIdOFallar(id);

      if (!proveedor.getNombre().equalsIgnoreCase(dto.getNombre()) &&
            proveedorRepository.existsByNombre(dto.getNombre())) {
         throw new BadRequestException("Ya existe un proveedor con ese nombre");
      }

      proveedor.actualizarNombre(dto.getNombre());
      proveedor.actualizarTelefono(dto.getTelefono());
      proveedor.actualizarCorreo(dto.getCorreo());

      return toDTO(proveedorRepository.save(proveedor));
   }

   @Override
   @Transactional
   public void eliminar(Long id) {
      if (!proveedorRepository.existsById(id)) {
         throw new ResourceNotFoundException("Proveedor no encontrado con id: " + id);
      }
      proveedorRepository.deleteById(id);
   }

   /* Helpers privados */

   private Proveedor buscarPorIdOFallar(Long id) {
      return proveedorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                  "Proveedor no encontrado con id: " + id));
   }

   private ProveedorResponseDTO toDTO(Proveedor p) {
      return new ProveedorResponseDTO(
            p.getId(),
            p.getNombre(),
            p.getTelefono(),
            p.getCorreo());
   }
}