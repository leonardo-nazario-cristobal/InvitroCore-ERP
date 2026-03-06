package com.invitrocore.auth;

public interface AuthService {

   AuthResponseDTO login(LoginRequestDTO dto);

   AuthResponseDTO register(RegisterRequestDTO dto);

   AuthResponseDTO refresh(RefreshRequestDTO dto);

   void logout(RefreshRequestDTO dto);
}
