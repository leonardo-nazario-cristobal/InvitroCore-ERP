package com.invitrocore.service;

import com.invitrocore.dto.UsuarioRequestDTO;
import com.invitrocore.dto.UsuarioResponseDTO;
import com.invitrocore.model.TipoRol;

import java.util.List;

public interface UsuarioService {

   UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto);

   UsuarioResponseDTO obtenerPorId(Long id);

   List<UsuarioResponseDTO> listarUsuarios();

   UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO dto);

   void desactivarUsuario(Long id);

   void activarUsuario(Long id);

   void cambiarRol(Long id, TipoRol nuevoRol);
}
