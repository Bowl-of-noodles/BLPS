package com.javadevjournal.exceptions;

import com.javadevjournal.dto.ExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionAPIHandler extends ResponseEntityExceptionHandler {

    private static Map<Object, Object> doModel(HttpStatus status, String message) {
        Map<Object, Object> model = new HashMap<>();
        model.put("status", status);
        model.put("message", message);
        return model;
    }

    @ExceptionHandler(NoAuthorityException.class)
    protected ResponseEntity<Object> Authorize(NoAuthorityException exception) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(doModel(HttpStatus.FORBIDDEN, "У вас нет нужных прав для этой команды"));
    }

    @ExceptionHandler({ AuthenticationException.class })
    @ResponseBody
    public ResponseEntity<NoAuthorityException> handleAuthenticationException(Exception ex) {

        NoAuthorityException re = new NoAuthorityException("Нет нужных прав");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(re);
    }

    /*@ExceptionHandler(NoAuthorityException.class)
    public ExceptionDTO handleNoAthorityException(NoAuthorityException e) {
        return new ExceptionDTO(403, e.getMessage());
    }*/
}
