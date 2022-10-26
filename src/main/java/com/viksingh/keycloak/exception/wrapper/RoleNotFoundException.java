package com.viksingh.keycloak.exception.wrapper;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class RoleNotFoundException extends RuntimeException {
    private String message;
    private HttpStatus statusCode;
    public RoleNotFoundException() {
        super();
    }

    public RoleNotFoundException(String message,HttpStatus statusCode) {
        super(message);
        this.message=message;
        this.statusCode=statusCode;
    }

    public RoleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RoleNotFoundException(Throwable cause) {
        super(cause);
    }

    protected RoleNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
