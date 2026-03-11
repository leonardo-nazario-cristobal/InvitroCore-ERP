package com.invitrocore.service;

import com.invitrocore.dto.ProductoRequestDTO;
import com.invitrocore.dto.ProductoResponseDTO;
import com.invitrocore.dto.ProductoStockBajoDTO;
import com.invitrocore.exception.BadRequestException;
import com.invitrocore.exception.ResourceNotFoundException;
import com.invitrocore.model.Categoria;
import com.invitrocore.model.Producto;
import com.invitrocore.repository.CategoriaRepository;
import com.invitrocore.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {

   private final ProductoRepository productoRepository;
   private final CategoriaRepository categoriaRepository;

   public ProductoServiceImpl(ProductoRepository productoRepository,
         CategoriaRepository categoriaRepository) {
      this.productoRepository = productoRepository;
      this.categoriaRepository = categoriaRepository;
   }

   /* Crear */

   @Override
   @Transactional
   public ProductoResponseDTO crear(ProductoRequestDTO dto) {

      if (productoRepository.existsByNombre(dto.getNombre())) {
         throw new BadRequestException("Ya existe un producto con ese nombre");
      }

      String codigo = (dto.getCodigoBarras() != null && !dto.getCodigoBarras().isBlank())
            ? dto.getCodigoBarras()
            : generarCodigoBarras();

      if (productoRepository.existsByCodigoBarras(codigo)) {
         throw new BadRequestException("Ya existe un producto con es código de barras");
      }

      Categoria categoria = resolverCategoria(dto.getIdCategoria());

      Producto producto = new Producto(
            codigo,
            dto.getNombre(),
            dto.getDescripcion(),
            dto.getPrecio(),
            dto.getStockMinimo(),
            categoria);
      return toDTO(productoRepository.save(producto));
   }

   /* Leer */

   @Override
   public ProductoResponseDTO obtenerPorId(Long id) {
      return toDTO(buscarPorIdOFallar(id));
   }

   @Override
   public ProductoResponseDTO obtenerPorCodigoBarras(String codigoBarras) {
      return productoRepository.findByCodigoBarras(codigoBarras)
            .map(this::toDTO)
            .orElseThrow(() -> new ResourceNotFoundException(
                  "Producto no encontrado con código: " + codigoBarras));
   }

   @Override
   public List<ProductoResponseDTO> listar() {
      return productoRepository.findByActivoTrue()
            .stream()
            .map(this::toDTO)
            .toList();
   }

   @Override
   public List<ProductoResponseDTO> listarPorCategoria(Long idCategoria) {
      return productoRepository.findByActivoTrueAndCategoriaId(idCategoria)
            .stream()
            .map(this::toDTO)
            .toList();
   }

   @Override
   public List<ProductoResponseDTO> buscarPorNombre(String nombre) {
      return productoRepository.findByActivoTrueAndNombreContainingIgnoreCase(nombre)
            .stream()
            .map(this::toDTO)
            .toList();
   }

   @Override
   public List<ProductoStockBajoDTO> listarStockBajo() {
      return productoRepository.findProductosConStockBajo()
            .stream()
            .map(this::toStockBajoDTO)
            .toList();
   }

   /* Actualizar */

   @Override
   @Transactional
   public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO dto) {

      Producto producto = buscarPorIdOFallar(id);

      if (!producto.getNombre().equalsIgnoreCase(dto.getNombre()) &&
            productoRepository.existsByNombre(dto.getNombre())) {
         throw new BadRequestException("Ya existe un producto con ese nombre");
      }

      if (dto.getCodigoBarras() != null &&
            !dto.getCodigoBarras().equals(producto.getCodigoBarras()) &&
            productoRepository.existsByCodigoBarras(dto.getCodigoBarras())) {
         throw new BadRequestException("Ya existe un producto con ese código de barras");
      }

      producto.actualizarNombre(dto.getNombre());
      producto.actualizarDescripcion(dto.getDescripcion());
      producto.actualizarPrecio(dto.getPrecio());
      producto.actualizarCodigoBarras(dto.getCodigoBarras());

      if (dto.getStockMinimo() != null) {
         producto.actualizarStockMinimo(dto.getStockMinimo());
      }

      producto.actualizarCategoria(resolverCategoria(dto.getIdCategoria()));

      return toDTO(productoRepository.save(producto));
   }

   /* Activar / Desactivar */

   @Override
   @Transactional
   public void desactivar(Long id) {
      Producto producto = buscarPorIdOFallar(id);
      producto.desactivar();
      productoRepository.save(producto);
   }

   @Override
   @Transactional
   public void activar(Long id) {
      Producto producto = buscarPorIdOFallar(id);
      producto.activar();
      productoRepository.save(producto);
   }

   /* Helpers privados */

   private Producto buscarPorIdOFallar(Long id) {
      return productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                  "Producto no encontrado con id: " + id));
   }

   private Categoria resolverCategoria(Long idCategoria) {
      if (idCategoria == null)
         return null;
      return categoriaRepository.findById(idCategoria)
            .orElseThrow(() -> new ResourceNotFoundException(
                  "Categoria no encontrada con el id: " + idCategoria));
   }

   private String generarCodigoBarras() {
      String prefijo = "770";
      String timestamp = String.valueOf(System.currentTimeMillis()).substring(7);
      String random = String.format("%03d", (int) (Math.random() * 1000));
      return prefijo + timestamp + random;
   }

   private ProductoResponseDTO toDTO(Producto p) {
      return new ProductoResponseDTO(
            p.getId(),
            p.getCodigoBarras(),
            p.getNombre(),
            p.getDescripcion(),
            p.getPrecio(),
            p.getCostoPromedio(),
            p.getStock(),
            p.getStockMinimo(),
            p.isActivo(),
            p.tieneStockBajo(),
            p.getCategoria() != null ? p.getCategoria().getId() : null,
            p.getCategoria() != null ? p.getCategoria().getNombre() : null);
   }

   private ProductoStockBajoDTO toStockBajoDTO(Producto p) {
      return new ProductoStockBajoDTO(
            p.getId(),
            p.getCodigoBarras(),
            p.getNombre(),
            p.getStock(),
            p.getStockMinimo(),
            p.getCostoPromedio());
   }
}
