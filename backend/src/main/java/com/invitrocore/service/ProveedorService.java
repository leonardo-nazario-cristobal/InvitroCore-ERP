package com.invitrocore.service;

import com.invitrocore.dto.ProveedorRequestDTO;
import com.invitrocore.dto.ProveedorResponseDTO;

import org.springframework.data.domain.Page;

public interface ProveedorService {

   ProveedorResponseDTO crear(ProveedorRequestDTO dto);

   ProveedorResponseDTO obtenerPorId(Long id);

   Page<ProveedorResponseDTO> listar(int pagina, int tamanio);

   ProveedorResponseDTO actualizar(Long id, ProveedorRequestDTO dto);

   void eliminar(Long id);
}