package com.invitrocore.service;

import com.invitrocore.dto.MovimientoInventarioRequestDTO;
import com.invitrocore.dto.MovimientoInventarioResponseDTO;
import com.invitrocore.exception.BadRequestException;
import com.invitrocore.exception.ResourceNotFoundException;
import com.invitrocore.model.MovimientoInventario;
import com.invitrocore.model.Producto;
import com.invitrocore.model.TipoMovimiento;
import com.invitrocore.model.Usuario;
import com.invitrocore.repository.MovimientoInventarioRepository;
import com.invitrocore.repository.ProductoRepository;
import com.invitrocore.repository.UsuarioRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class MovimientoInventarioServiceImpl implements MovimientoInventarioService {

   private final MovimientoInventarioRepository movimientoRepository;
   private final ProductoRepository productoRepository;
   private final UsuarioRepository usuarioRepository;

   public MovimientoInventarioServiceImpl(
         MovimientoInventarioRepository movimientoRepository,
         ProductoRepository productoRepository,
         UsuarioRepository usuarioRepository) {

      this.movimientoRepository = movimientoRepository;
      this.productoRepository = productoRepository;
      this.usuarioRepository = usuarioRepository;
   }

   /* Registrar */

   @Override
   @Transactional
   public MovimientoInventarioResponseDTO registrar(MovimientoInventarioRequestDTO dto,
         String correoUsuario) {

      Producto producto = buscarProductoActivoOFallar(dto.getIdProducto());
      Usuario usuario = buscarUsuarioActivoOFallar(correoUsuario);

      aplicarMovimientoAlStock(producto, dto.getTipo(), dto.getCantidad());

      MovimientoInventario movimiento = new MovimientoInventario(
            producto,
            dto.getTipo(),
            dto.getCantidad(),
            dto.getMotivo(),
            usuario);

      productoRepository.save(producto);
      return toDTO(movimientoRepository.save(movimiento));
   }

   /* Leer */

   @Override
   public MovimientoInventarioResponseDTO obtenerPorId(Long id) {
      return toDTO(movimientoRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                  "Movimiento no encontrado con id: " + id)));
   }

   @Override
   public Page<MovimientoInventarioResponseDTO> listar(int pagina, int tamanio) {
      Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by("creadoEn").descending());
      return movimientoRepository.findAll(pageable).map(this::toDTO);
   }

   @Override
   public List<MovimientoInventarioResponseDTO> listarPorProducto(Long idProducto) {
      buscarProductoActivoOFallar(idProducto);
      return movimientoRepository.findByProductoIdOrderByCreadoEnDesc(idProducto)
            .stream()
            .map(this::toDTO)
            .toList();
   }

   @Override
   public Page<MovimientoInventarioResponseDTO> listarPorTipo(TipoMovimiento tipo, int pagina, int tamanio) {
      Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by("creadoEn").descending());
      return movimientoRepository.findByTipoOrderByCreadoEnDesc(tipo, pageable).map(this::toDTO);
   }

   @Override
   public List<MovimientoInventarioResponseDTO> listarPorUsuario(Long idUsuario) {
      if (!usuarioRepository.existsById(idUsuario)) {
         throw new ResourceNotFoundException(
               "Usuario no encontrado con id: " + idUsuario);
      }
      return movimientoRepository.findByUsuarioIdOrderByCreadoEnDesc(idUsuario)
            .stream()
            .map(this::toDTO)
            .toList();
   }

   @Override
   public List<MovimientoInventarioResponseDTO> listarPorRangoFecha(OffsetDateTime desde,
         OffsetDateTime hasta) {
      if (desde.isAfter(hasta)) {
         throw new BadRequestException(
               "La fecha de inicio no puede ser mayor a la fecha fin");
      }
      return movimientoRepository.findByCreadoEnBetweenOrderByCreadoEnDesc(desde, hasta)
            .stream()
            .map(this::toDTO)
            .toList();
   }

   /* Helpers privados */

   private Producto buscarProductoActivoOFallar(Long id) {
      Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                  "Producto no encontrado con id: " + id));

      if (!producto.isActivo()) {
         throw new BadRequestException(
               "El producto '" + producto.getNombre() + "' está inactivo");
      }

      return producto;
   }

   private Usuario buscarUsuarioActivoOFallar(String correo) {
      return usuarioRepository.findByCorreoAndActivoTrue(correo)
            .orElseThrow(() -> new ResourceNotFoundException(
                  "Usuario no encontrado o inactivo: " + correo));
   }

   private void aplicarMovimientoAlStock(Producto producto,
         TipoMovimiento tipo,
         Integer cantidad) {

      if (tipo == TipoMovimiento.ENTRADA) {
         producto.agregarStock(cantidad);
      } else if (tipo == TipoMovimiento.SALIDA) {
         producto.reducirStock(cantidad);
      } else if (tipo == TipoMovimiento.AJUSTE) {
         producto.agregarStock(cantidad);
      }
   }

   private MovimientoInventarioResponseDTO toDTO(MovimientoInventario m) {
      return new MovimientoInventarioResponseDTO(
            m.getId(),
            m.getProducto().getId(),
            m.getProducto().getNombre(),
            m.getProducto().getCodigoBarras(),
            m.getTipo(),
            m.getCantidad(),
            m.getMotivo(),
            m.getUsuario() != null ? m.getUsuario().getId() : null,
            m.getUsuario() != null ? m.getUsuario().getNombre() : null,
            m.getCreadoEn());
   }
}
