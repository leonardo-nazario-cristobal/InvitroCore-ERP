package com.invitrocore.service;

import com.invitrocore.dto.MovimientoInventarioRequestDTO;
import com.invitrocore.dto.MovimientoInventarioResponseDTO;
import com.invitrocore.model.TipoMovimiento;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Page;

public interface MovimientoInventarioService {

   MovimientoInventarioResponseDTO registrar(MovimientoInventarioRequestDTO dto,
         String correoUsuario);

   MovimientoInventarioResponseDTO obtenerPorId(Long id);

   Page<MovimientoInventarioResponseDTO> listar(int pagina, int tamanio);

   List<MovimientoInventarioResponseDTO> listarPorProducto(Long idProducto);

   Page<MovimientoInventarioResponseDTO> listarPorTipo(TipoMovimiento tipo, int pagina, int tamanio);

   List<MovimientoInventarioResponseDTO> listarPorUsuario(Long idUsuario);

   List<MovimientoInventarioResponseDTO> listarPorRangoFecha(OffsetDateTime desde,
         OffsetDateTime hasta);
}
