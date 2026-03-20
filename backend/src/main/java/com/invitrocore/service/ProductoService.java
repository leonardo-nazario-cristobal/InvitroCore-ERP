package com.invitrocore.service;

import com.invitrocore.dto.ProductoRequestDTO;
import com.invitrocore.dto.ProductoResponseDTO;
import com.invitrocore.dto.ProductoStockBajoDTO;

import java.util.List;

public interface ProductoService {

   ProductoResponseDTO crear(ProductoRequestDTO dto);

   ProductoResponseDTO obtenerPorId(Long id);

   ProductoResponseDTO obtenerPorCodigoBarras(String codigoBarras);

   List<ProductoResponseDTO> listar();

   List<ProductoResponseDTO> listarInactivos();

   List<ProductoResponseDTO> listarTodos();

   List<ProductoResponseDTO> listarPorCategoria(Long idCategoria);

   List<ProductoResponseDTO> buscarPorNombre(String nombre);

   List<ProductoStockBajoDTO> listarStockBajo();

   ProductoResponseDTO actualizar(Long id, ProductoRequestDTO dto);

   void desactivar(Long id);

   void activar(Long id);
}
