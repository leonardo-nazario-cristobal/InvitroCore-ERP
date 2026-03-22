package com.invitrocore.service;

import com.invitrocore.dto.VentaRequestDTO;
import com.invitrocore.dto.VentaResponseDTO;
import com.invitrocore.model.EstadoVenta;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Page;

public interface VentaService {

   VentaResponseDTO registrar(VentaRequestDTO dto, String correoUsuario);

   VentaResponseDTO obtenerPorId(Long id);

   Page<VentaResponseDTO> listar(int pagina, int tamanio);

   List<VentaResponseDTO> listarPorUsuario(Long idUsuario);

   Page<VentaResponseDTO> listarPorEstado(EstadoVenta estado, int pagina, int tamanio);

   List<VentaResponseDTO> listarPorRangoFecha(OffsetDateTime desde, OffsetDateTime hasta);

   VentaResponseDTO cancelar(Long id, String correoUsuario);
}
