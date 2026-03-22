package com.invitrocore.service;

import com.invitrocore.dto.DetalleVentaRequestDTO;
import com.invitrocore.dto.DetalleVentaResponseDTO;
import com.invitrocore.dto.VentaRequestDTO;
import com.invitrocore.dto.VentaResponseDTO;
import com.invitrocore.exception.BadRequestException;
import com.invitrocore.exception.ResourceNotFoundException;
import com.invitrocore.model.*;
import com.invitrocore.repository.ProductoRepository;
import com.invitrocore.repository.MovimientoInventarioRepository;
import com.invitrocore.repository.UsuarioRepository;
import com.invitrocore.repository.VentaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class VentaServiceImpl implements VentaService {

   private final VentaRepository ventaRepository;
   private final ProductoRepository productoRepository;
   private final UsuarioRepository usuarioRepository;
   private final MovimientoInventarioRepository movimientoRepository;

   public VentaServiceImpl(VentaRepository ventaRepository,
         ProductoRepository productoRepository,
         UsuarioRepository usuarioRepository,
         MovimientoInventarioRepository movimientoRepository) {

      this.ventaRepository = ventaRepository;
      this.productoRepository = productoRepository;
      this.usuarioRepository = usuarioRepository;
      this.movimientoRepository = movimientoRepository;
   }

   /* Registrar */

   @Override
   @Transactional
   public VentaResponseDTO registrar(VentaRequestDTO dto, String correoUsuario) {

      Usuario usuario = buscarUsuarioActivoOFallar(correoUsuario);

      validarProductosDuplicados(dto);

      Venta venta = new Venta(dto.getMetodoPago(), usuario);

      for (DetalleVentaRequestDTO detalleDTO : dto.getDetalles()) {
         Producto producto = buscarProductoActivoOFallar(detalleDTO.getIdProducto());
         validarStockSuficiente(producto, detalleDTO.getCantidad());

         BigDecimal precio = detalleDTO.getPrecioUnitario() != null
               ? detalleDTO.getPrecioUnitario()
               : producto.getPrecio();

         DetalleVenta detalle = new DetalleVenta(venta, producto, detalleDTO.getCantidad(), precio);
         venta.agregarDetalle(detalle);
         producto.reducirStock(detalleDTO.getCantidad());
         productoRepository.save(producto);

         movimientoRepository.save(new MovimientoInventario(
               producto,
               TipoMovimiento.SALIDA,
               detalleDTO.getCantidad(),
               "Venta registrada",
               usuario));
      }

      return toDTO(ventaRepository.save(venta));
   }

   /* Leer */

   @Override
   public VentaResponseDTO obtenerPorId(Long id) {
      Venta venta = ventaRepository.findByWithDetalles(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                  "Venta no encontrada con id: " + id));
      return toDTO(venta);
   }

   @Override
   public Page<VentaResponseDTO> listar(int pagina, int tamanio) {
      Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by("fecha").ascending());
      return ventaRepository.findAll(pageable)
            .map(this::toDTO);
   }

   @Override
   public List<VentaResponseDTO> listarPorUsuario(Long idUsuario) {
      if (!usuarioRepository.existsById(idUsuario)) {
         throw new ResourceNotFoundException("Usuario no encontrado con id: " + idUsuario);
      }

      return ventaRepository.findByUsuarioId(idUsuario)
            .stream()
            .map(this::toDTO)
            .toList();
   }

   @Override
   public Page<VentaResponseDTO> listarPorEstado(EstadoVenta estado, int pagina, int tamanio) {
      Pageable pageable = PageRequest.of(pagina, tamanio, Sort.by("fecha").descending());
      return ventaRepository.findByEstado(estado, pageable).map(this::toDTO);
   }

   @Override
   public List<VentaResponseDTO> listarPorRangoFecha(OffsetDateTime desde, OffsetDateTime hasta) {
      if (desde.isAfter(hasta)) {
         throw new BadRequestException("La fecha de inicio no puede ser mayor a la fecha fin");
      }

      return ventaRepository.findByFechaBetween(desde, hasta)
            .stream()
            .map(this::toDTO)
            .toList();
   }

   /* Cancelar */

   @Override
   @Transactional
   public VentaResponseDTO cancelar(Long id, String correoUsuario) {

      Venta venta = ventaRepository.findByWithDetalles(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                  "Venta no encontrada con id: " + id));

      venta.cancelar();

      // ← buscar usuario igual que en registrar
      Usuario usuario = buscarUsuarioActivoOFallar(correoUsuario);

      for (DetalleVenta detalle : venta.getDetalles()) {
         Producto producto = detalle.getProducto();
         producto.agregarStock(detalle.getCantidad());
         productoRepository.save(producto);

         movimientoRepository.save(new MovimientoInventario(
               producto,
               TipoMovimiento.ENTRADA,
               detalle.getCantidad(),
               "Cancelación de venta #" + venta.getId(),
               usuario // ← ya no es null
         ));
      }

      return toDTO(ventaRepository.save(venta));
   }

   /* Helpers privados */

   private Usuario buscarUsuarioActivoOFallar(String correo) {
      return usuarioRepository.findByCorreoAndActivoTrue(correo)
            .orElseThrow(() -> new ResourceNotFoundException(
                  "Usuario no encontrado o inactivo: " + correo));
   }

   private Producto buscarProductoActivoOFallar(Long id) {
      Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                  "Producto no encontrado con id: " + id));

      if (!producto.isActivo()) {
         throw new BadRequestException("El producto '" + producto.getNombre() + "' está inactivo");
      }
      return producto;
   }

   private void validarStockSuficiente(Producto producto, Integer cantidad) {
      if (cantidad > producto.getStock()) {
         throw new BadRequestException(
               "Stock insuficiente para '" + producto.getNombre() +
                     "'. Disponible: " + producto.getStock() +
                     ", solicitado: " + cantidad);
      }
   }

   private void validarProductosDuplicados(VentaRequestDTO dto) {
      long productosUnicos = dto.getDetalles().stream()
            .map(DetalleVentaRequestDTO::getIdProducto)
            .distinct()
            .count();

      if (productosUnicos != dto.getDetalles().size()) {
         throw new BadRequestException(
               "La venta tiene productos duplicados, agrupa la cantidad en un solo detalle");
      }
   }

   private VentaResponseDTO toDTO(Venta v) {
      List<DetalleVentaResponseDTO> detalles = v.getDetalles()
            .stream()
            .map(this::toDetalleDTO)
            .toList();

      return new VentaResponseDTO(
            v.getId(),
            v.getFecha(),
            v.getUsuario().getId(),
            v.getUsuario().getNombre(),
            v.getMetodoPago(),
            v.getEstado(),
            v.getTotal(),
            detalles);
   }

   private DetalleVentaResponseDTO toDetalleDTO(DetalleVenta d) {
      return new DetalleVentaResponseDTO(
            d.getProducto().getId(),
            d.getProducto().getNombre(),
            d.getProducto().getCodigoBarras(),
            d.getCantidad(),
            d.getPrecioUnitario(),
            d.getSubtotal());
   }
}
