package com.invitrocore.service;

import com.invitrocore.dto.CompraRequestDTO;
import com.invitrocore.dto.CompraResponseDTO;
import com.invitrocore.dto.DetalleCompraRequestDTO;
import com.invitrocore.dto.DetalleCompraResponseDTO;
import com.invitrocore.exception.BadRequestException;
import com.invitrocore.exception.ResourceNotFoundException;
import com.invitrocore.model.Compra;
import com.invitrocore.model.DetalleCompra;
import com.invitrocore.model.Producto;
import com.invitrocore.model.Proveedor;
import com.invitrocore.repository.CompraRepository;
import com.invitrocore.repository.ProductoRepository;
import com.invitrocore.repository.ProveedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class CompraServiceImpl implements CompraService {

   private final CompraRepository compraRepository;
   private final ProveedorRepository proveedorRepository;
   private final ProductoRepository productoRepository;

   public CompraServiceImpl(CompraRepository compraRepository,
         ProveedorRepository proveedorRepository,
         ProductoRepository productoRepository) {
      this.compraRepository = compraRepository;
      this.proveedorRepository = proveedorRepository;
      this.productoRepository = productoRepository;
   }

   /* Registrar */

   @Override
   @Transactional
   public CompraResponseDTO registrar(CompraRequestDTO dto) {

      Proveedor proveedor = buscarProveedorOFallar(dto.getIdProveedor());

      validarProductosDuplicados(dto);

      Compra compra = new Compra(proveedor);

      for (DetalleCompraRequestDTO detalleDTO : dto.getDetalles()) {
         Producto producto = buscarProductoActivoOFallar(detalleDTO.getIdProducto());

         DetalleCompra detalle = new DetalleCompra(compra,
               producto,
               detalleDTO.getCantidad(),
               detalleDTO.getCostoUnitario());

         compra.agregarDetalle(detalle);
         producto.agregarStock(detalleDTO.getCantidad());
         recalcularCostoPromedio(producto, detalleDTO.getCantidad(), detalleDTO.getCostoUnitario());
         productoRepository.save(producto);
      }

      return toDTO(compraRepository.save(compra));
   }

   /* Leer */

   @Override
   public CompraResponseDTO obtenerPorId(Long id) {
      Compra compra = compraRepository.findByIdWithDetalles(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                  "Compra no encontrada con id: " + id));

      return toDTO(compra);
   }

   @Override
   public List<CompraResponseDTO> listar() {
      return compraRepository.findAll()
            .stream()
            .map(this::toDTO)
            .toList();
   }

   @Override
   public List<CompraResponseDTO> listarPorProveedor(Long idProveedor) {
      buscarProductoActivoOFallar(idProveedor);
      return compraRepository.findByProveedorId(idProveedor)
            .stream()
            .map(this::toDTO)
            .toList();
   }

   @Override
   public List<CompraResponseDTO> listarPorRangoFecha(OffsetDateTime desde, OffsetDateTime hasta) {
      if (desde.isAfter(hasta)) {
         throw new BadRequestException("La fecha de inicio no puede ser mayor a la fecha de fin");
      }

      return compraRepository.findByFechaBetween(desde, hasta)
            .stream()
            .map(this::toDTO)
            .toList();
   }

   /* Helpers privados */

   private Proveedor buscarProveedorOFallar(Long id) {
      return proveedorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                  "Proveedor no encontrado con id: " + id));
   }

   private Producto buscarProductoActivoOFallar(Long id) {
      Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                  "Producto no encontrado con id: " + id));
      if (!producto.isActivo()) {
         throw new BadRequestException("El producto '" + producto.getNombre() + "' esta inactivo");
      }

      return producto;
   }

   private void validarProductosDuplicados(CompraRequestDTO dto) {
      long productosUnicos = dto.getDetalles().stream()
            .map(DetalleCompraRequestDTO::getIdProducto)
            .distinct()
            .count();
      if (productosUnicos != dto.getDetalles().size()) {
         throw new BadRequestException("La compra tiene productos duplicados, agrupa la cantidad en un solo detalle");
      }
   }

   private void recalcularCostoPromedio(Producto producto,
         Integer cantidadNueva,
         BigDecimal costoNuevo) {
      BigDecimal stockActual = BigDecimal.valueOf(producto.getStock() - cantidadNueva);
      BigDecimal costoActual = producto.getCostoPromedio();

      BigDecimal costoTotalActual = stockActual.multiply(costoActual);
      BigDecimal costoTotalNuevo = BigDecimal.valueOf(cantidadNueva).multiply(costoNuevo);
      BigDecimal stockTotal = stockActual.add(BigDecimal.valueOf(cantidadNueva));

      if (stockTotal.compareTo(BigDecimal.ZERO) == 0) {
         producto.actualizarCostoPromedio(costoNuevo);
         return;
      }

      BigDecimal nuevoCosto = costoTotalActual
            .add(costoTotalNuevo)
            .divide(stockTotal, 2, RoundingMode.HALF_UP);

      producto.actualizarCostoPromedio(nuevoCosto);
   }

   private CompraResponseDTO toDTO(Compra c) {

      List<DetalleCompraResponseDTO> detalles = c.getDetalles()
            .stream()
            .map(this::toDetalleDTO)
            .toList();

      return new CompraResponseDTO(
            c.getId(),
            c.getFecha(),
            c.getProveedor().getId(),
            c.getProveedor().getNombre(),
            c.getTotal(),
            detalles);
   }

   private DetalleCompraResponseDTO toDetalleDTO(DetalleCompra d) {
      return new DetalleCompraResponseDTO(
            d.getProducto().getId(),
            d.getProducto().getNombre(),
            d.getProducto().getCodigoBarras(),
            d.getCantidad(),
            d.getCostoUnitario(),
            d.getSubtotal());
   }
}
