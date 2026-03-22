package com.invitrocore.service;

import com.invitrocore.dto.UsuarioRequestDTO;
import com.invitrocore.dto.UsuarioResponseDTO;
import com.invitrocore.model.TipoRol;
import org.springframework.data.domain.Page;

public interface UsuarioService {

   UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto);

   UsuarioResponseDTO obtenerPorId(Long id);

   Page<UsuarioResponseDTO> listarUsuarios(int pagina, int tamanio);

   Page<UsuarioResponseDTO> listarInactivos(int pagina, int tamanio);

   Page<UsuarioResponseDTO> listarTodos(int pagina, int tamanios);

   UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO dto);

   void desactivarUsuario(Long id);

   void activarUsuario(Long id);

   void cambiarRol(Long id, TipoRol nuevoRol);
}
