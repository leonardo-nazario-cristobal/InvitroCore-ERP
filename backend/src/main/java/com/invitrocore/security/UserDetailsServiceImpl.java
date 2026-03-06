package com.invitrocore.security;

import com.invitrocore.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

   private final UsuarioRepository usuarioRepository;

   public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
      this.usuarioRepository = usuarioRepository;
   }

   @Override
   public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {

      return usuarioRepository.findByCorreoAndActivoTrue(correo)
            .map(usuario -> new User(
                  usuario.getCorreo(),
                  usuario.getPassword(),
                  List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getValor().toUpperCase()))))
            .orElseThrow(() -> new UsernameNotFoundException(
                  "Usuario no encontrado: " + correo));
   }
}