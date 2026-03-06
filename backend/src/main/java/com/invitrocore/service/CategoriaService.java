package com.invitrocore.service;

import com.invitrocore.dto.CategoriaRequestDTO;
import com.invitrocore.dto.CategoriaResponseDTO;

import java.util.List;

public interface CategoriaService {

   CategoriaResponseDTO crear(CategoriaRequestDTO dto);

   CategoriaResponseDTO obtenerPorId(Long id);

   List<CategoriaResponseDTO> listar();

   CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO dto);

   void eliminar(Long id);
}
