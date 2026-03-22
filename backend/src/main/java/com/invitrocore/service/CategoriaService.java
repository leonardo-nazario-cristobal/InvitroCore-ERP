package com.invitrocore.service;

import com.invitrocore.dto.CategoriaRequestDTO;
import com.invitrocore.dto.CategoriaResponseDTO;
import org.springframework.data.domain.Page;

public interface CategoriaService {

   CategoriaResponseDTO crear(CategoriaRequestDTO dto);

   CategoriaResponseDTO obtenerPorId(Long id);

   Page<CategoriaResponseDTO> listar(int pagina, int tamanio);

   CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO dto);

   void eliminar(Long id);
}
