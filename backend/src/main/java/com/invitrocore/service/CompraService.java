package com.invitrocore.service;

import com.invitrocore.dto.CompraRequestDTO;
import com.invitrocore.dto.CompraResponseDTO;

import java.time.OffsetDateTime;
import java.util.List;

public interface CompraService {

   CompraResponseDTO registrar(CompraRequestDTO dto);

   CompraResponseDTO obtenerPorId(Long id);

   List<CompraResponseDTO> listar();

   List<CompraResponseDTO> listarPorProveedor(Long idProveedor);

   List<CompraResponseDTO> listarPorRangoFecha(OffsetDateTime desde, OffsetDateTime hasta);
}
