package com.invitrocore.exception;

public class ResourceNotFoundException extends RuntimeException {

   public ResourceNotFoundException(String message) {
      super(message);
   }
}
