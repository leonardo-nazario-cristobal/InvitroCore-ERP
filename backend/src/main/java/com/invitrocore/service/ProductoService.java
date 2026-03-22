package com.invitrocore.service;

import com.invitrocore.dto.ProductoRequestDTO;
import com.invitrocore.dto.ProductoResponseDTO;
import org.springframework.data.domain.Page;
import com.invitrocore.dto.ProductoStockBajoDTO;

import java.util.List;

public interface ProductoService {

   ProductoResponseDTO crear(ProductoRequestDTO dto);

   ProductoResponseDTO obtenerPorId(Long id);

   ProductoResponseDTO obtenerPorCodigoBarras(String codigoBarras);

   Page<ProductoResponseDTO> listar(int pagina, int tamanio);

   Page<ProductoResponseDTO> listarInactivos(int pagina, int tamanio);

   Page<ProductoResponseDTO> listarTodos(int pagina, int tamanio);

   Page<ProductoResponseDTO> listarPorCategoria(Long idCategoria, int pagina, int tamanio);

   Page<ProductoResponseDTO> buscarPorNombre(String nombre, int pagina, int tamanio);

   List<ProductoStockBajoDTO> listarStockBajo();

   ProductoResponseDTO actualizar(Long id, ProductoRequestDTO dto);

   void desactivar(Long id);

   void activar(Long id);
}
