package com.rj.ecommerce_backend.securityconfig.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TokenBlacklistException extends RuntimeException {
  public TokenBlacklistException(String message, Throwable cause) {
    super(message, cause);
  }
}
