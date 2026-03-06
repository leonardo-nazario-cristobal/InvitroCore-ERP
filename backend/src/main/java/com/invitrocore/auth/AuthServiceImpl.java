package com.invitrocore.auth;

import com.invitrocore.exception.BadRequestException;
import com.invitrocore.model.TipoRol;
import com.invitrocore.model.Usuario;
import com.invitrocore.repository.UsuarioRepository;
import com.invitrocore.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

   private final UsuarioRepository usuarioRepository;
   private final PasswordEncoder passwordEncoder;
   private final JwtService jwtService;
   private final AuthenticationManager authenticationManager;
   private final UserDetailsService userDetailsService;

   private final Set<String> refreshTokensValidos = new HashSet<>();

   public AuthServiceImpl(UsuarioRepository usuarioRepository,
         PasswordEncoder passwordEncoder,
         JwtService jwtService,
         AuthenticationManager authenticationManager,
         UserDetailsService userDetailsService) {

      this.usuarioRepository = usuarioRepository;
      this.passwordEncoder = passwordEncoder;
      this.jwtService = jwtService;
      this.authenticationManager = authenticationManager;
      this.userDetailsService = userDetailsService;
   }

   /* Login */

   @Override
   public AuthResponseDTO login(LoginRequestDTO dto) {

      var authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                  dto.getCorreo(),
                  dto.getPassword()));

      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      String accessToken = jwtService.generarAccessToken(userDetails);
      String refreshToken = jwtService.generarRefreshToken(userDetails);
      refreshTokensValidos.add(refreshToken);

      return new AuthResponseDTO(accessToken, refreshToken);
   }

   /* Register */

   @Override
   @Transactional
   public AuthResponseDTO register(RegisterRequestDTO dto) {

      if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
         throw new BadRequestException("El Correo Ya Esta Registrado");
      }

      TipoRol rol = dto.getRol() != null ? dto.getRol() : TipoRol.CAJERO;

      Usuario usuario = new Usuario(
            dto.getNombre(),
            dto.getCorreo(),
            passwordEncoder.encode(dto.getPassword()),
            rol);

      usuarioRepository.save(usuario);

      UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getCorreo());
      String accessToken = jwtService.generarAccessToken(userDetails);
      String rerefreshToken = jwtService.generarRefreshToken(userDetails);

      refreshTokensValidos.add(rerefreshToken);

      return new AuthResponseDTO(accessToken, rerefreshToken);
   }

   /* Refresh */

   @Override
   public AuthResponseDTO refresh(RefreshRequestDTO dto) {

      String refreshToken = dto.getRefreshToken();

      if (!refreshTokensValidos.contains(refreshToken)) {
         throw new BadRequestException("Refresh Token Inválido O Expirado");
      }

      String correo = jwtService.extraerCorreo(refreshToken);

      UserDetails userDetails = userDetailsService.loadUserByUsername(correo);

      if (!jwtService.esValido(refreshToken, userDetails)) {
         refreshTokensValidos.remove(refreshToken);
         throw new BadRequestException("Refresh Token Inválido O Expirado");
      }

      refreshTokensValidos.remove(refreshToken);
      String nuevoAccessToken = jwtService.generarAccessToken(userDetails);
      String nuevoRefreshToken = jwtService.generarRefreshToken(userDetails);
      refreshTokensValidos.add(nuevoRefreshToken);

      return new AuthResponseDTO(nuevoAccessToken, nuevoRefreshToken);
   }

   /* Logout */

   @Override
   public void logout(RefreshRequestDTO dto) {
      refreshTokensValidos.remove(dto.getRefreshToken());
   }
}
