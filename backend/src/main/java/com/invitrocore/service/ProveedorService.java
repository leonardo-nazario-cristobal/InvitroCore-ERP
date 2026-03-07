package com.invitrocore.service;

import com.invitrocore.dto.ProveedorRequestDTO;
import com.invitrocore.dto.ProveedorResponseDTO;

import java.util.List;

public interface ProveedorService {

   ProveedorResponseDTO crear(ProveedorRequestDTO dto);

   ProveedorResponseDTO obtenerPorId(Long id);

   List<ProveedorResponseDTO> listar();

   ProveedorResponseDTO actualizar(Long id, ProveedorRequestDTO dto);

   void eliminar(Long id);
}