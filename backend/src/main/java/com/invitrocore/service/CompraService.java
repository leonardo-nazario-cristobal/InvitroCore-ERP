package com.invitrocore.service;

import com.invitrocore.dto.CompraRequestDTO;
import com.invitrocore.dto.CompraResponseDTO;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Page;

public interface CompraService {

   CompraResponseDTO registrar(CompraRequestDTO dto, String correoUsuario);

   CompraResponseDTO obtenerPorId(Long id);

   Page<CompraResponseDTO> listar(int pagina, int tamanio);

   List<CompraResponseDTO> listarPorProveedor(Long idProveedor);

   List<CompraResponseDTO> listarPorRangoFecha(OffsetDateTime desde, OffsetDateTime hasta);
}
