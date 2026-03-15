package com.invitrocore.service;

import com.invitrocore.dto.VentaRequestDTO;
import com.invitrocore.dto.VentaResponseDTO;
import com.invitrocore.model.EstadoVenta;

import java.time.OffsetDateTime;
import java.util.List;

public interface VentaService {

   VentaResponseDTO registrar(VentaRequestDTO dto, String correoUsuario);

   VentaResponseDTO obtenerPorId(Long id);

   List<VentaResponseDTO> listar();

   List<VentaResponseDTO> listarPorUsuario(Long idUsuario);

   List<VentaResponseDTO> listarPorEstado(EstadoVenta estado);

   List<VentaResponseDTO> listarPorRangoFecha(OffsetDateTime desde, OffsetDateTime hasta);

   VentaResponseDTO cancelar(Long id, String correoUsuario);
}
