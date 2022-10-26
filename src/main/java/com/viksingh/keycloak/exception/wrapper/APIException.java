package com.viksingh.keycloak.exception.wrapper;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class APIException extends RuntimeException{

    private static final long serialVersionUID = 1L;
    private final HttpStatus httpStatus;
    private final String status;
    private final String message;

    public APIException(HttpStatus httpStatus, String status, String message) {
        this.httpStatus = httpStatus;
        this.status = status;
        this.message = message;
    }

    public APIException(Throwable cause, HttpStatus httpStatus, String status, String message) {
        super(cause);
        this.httpStatus = httpStatus;
        this.status = status;
        this.message = message;
    }
}
