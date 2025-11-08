package org.skypro.skyshop.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ShopControllerAdvice {
    @ExceptionHandler(NoSuchProductException.class)
    public ResponseEntity<ShopError> noSuchProductException(NoSuchProductException e) {
        return new ResponseEntity<>(
                new ShopError("PRODUCT_NOT_FOUND", e.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }
}
