package com.invitrocore.service;

import com.invitrocore.dto.DetalleVentaRequestDTO;
import com.invitrocore.dto.DetalleVentaResponseDTO;
import com.invitrocore.dto.VentaRequestDTO;
import com.invitrocore.dto.VentaResponseDTO;
import com.invitrocore.exception.BadRequestException;
import com.invitrocore.exception.ResourceNotFoundException;
import com.invitrocore.model.*;
import com.invitrocore.repository.ProductoRepository;
import com.invitrocore.repository.UsuarioRepository;
import com.invitrocore.repository.VentaRepository;
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

   public VentaServiceImpl(VentaRepository ventaRepository,
         ProductoRepository productoRepository,
         UsuarioRepository usuarioRepository) {

      this.ventaRepository = ventaRepository;
      this.productoRepository = productoRepository;
      this.usuarioRepository = usuarioRepository;
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
   public List<VentaResponseDTO> listar() {
      return ventaRepository.findAll()
            .stream()
            .map(this::toDTO)
            .toList();
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
   public List<VentaResponseDTO> listarPorEstado(EstadoVenta estado) {
      return ventaRepository.findByEstado(estado)
            .stream()
            .map(this::toDTO)
            .toList();
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

      for (DetalleVenta detalle : venta.getDetalles()) {
         Producto producto = detalle.getProducto();
         producto.agregarStock(detalle.getCantidad());
         productoRepository.save(producto);
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
            d.getPrecioUnutario(),
            d.getSubtotal());
   }
}
