package com.nhnacademy.byeol23backend.commons.exception;

public class PermissionDeniedException extends RuntimeException {
  public PermissionDeniedException(String message) {
    super(message);
  }
}
