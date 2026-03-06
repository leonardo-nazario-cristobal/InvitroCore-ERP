package com.invitrocore.exception;

import java.time.LocalDateTime;

public record ApiError(
      boolean success,
      String message,
      int status,
      LocalDateTime timestamp) {

   public ApiError(String message, int status) {
      this(false, message, status, LocalDateTime.now());
   }
}