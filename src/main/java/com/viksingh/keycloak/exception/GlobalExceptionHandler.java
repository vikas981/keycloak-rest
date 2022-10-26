package com.viksingh.keycloak.exception;

import com.viksingh.keycloak.exception.payload.ExceptionMsg;
import com.viksingh.keycloak.exception.wrapper.APIException;
import com.viksingh.keycloak.exception.wrapper.RoleNotFoundException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {


	@ExceptionHandler(value = {APIException.class})
	public ResponseEntity<ExceptionMsg> handleAPIException(APIException apiException) {
		log.info("**GlobalExceptionHandler, handleAPIException exception*\n");
		return new ResponseEntity<>(
				ExceptionMsg.builder()
						.message(apiException.getMessage())
						.status(apiException.getStatus())
						.timestamp(ZonedDateTime.now(ZoneId.systemDefault()))
						.build(),apiException.getHttpStatus());
	}
	
	@ExceptionHandler(value = {RoleNotFoundException.class})
	public ResponseEntity<ExceptionMsg> handleAPIException(RoleNotFoundException exception) {
		log.info("**GlobalExceptionHandler, handleAPIException exception*\n");
		return new ResponseEntity<>(
				ExceptionMsg.builder()
					.message(exception.getMessage())
					.status(exception.getStatusCode().getReasonPhrase())
					.timestamp(ZonedDateTime.now(ZoneId.systemDefault()))
					.build(), exception.getStatusCode());
	}


}










