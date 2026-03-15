package com.invitrocore.service;

import com.invitrocore.dto.MovimientoInventarioRequestDTO;
import com.invitrocore.dto.MovimientoInventarioResponseDTO;
import com.invitrocore.model.TipoMovimiento;

import java.time.OffsetDateTime;
import java.util.List;

public interface MovimientoInventarioService {

   MovimientoInventarioResponseDTO registrar(MovimientoInventarioRequestDTO dto,
         String correoUsuario);

   MovimientoInventarioResponseDTO obtenerPorId(Long id);

   List<MovimientoInventarioResponseDTO> listar();

   List<MovimientoInventarioResponseDTO> listarPorProducto(Long idProducto);

   List<MovimientoInventarioResponseDTO> listarPorTipo(TipoMovimiento tipo);

   List<MovimientoInventarioResponseDTO> listarPorUsuario(Long idUsuario);

   List<MovimientoInventarioResponseDTO> listarPorRangoFecha(OffsetDateTime desde,
         OffsetDateTime hasta);
}
