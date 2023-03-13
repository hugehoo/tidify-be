package tidify.tidify.exception;

import java.sql.SQLIntegrityConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<?> handleResourceNotFoundException(Exception exception) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
            .message(exception.getMessage())
            .errorCode(((ResourceNotFoundException)exception).getErrorCode())
            .build();
        log.error("ResourceNotFoundException(): " + exceptionDto.toString());
        return new ResponseEntity<>(exceptionDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public final ResponseEntity<?> handleSQLViolationException(SQLIntegrityConstraintViolationException exception) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
            .message(exception.getMessage())
            .errorCode(ErrorTypes.SQL_VIOLATION_EXCEPTION.getCode())
            .build();
        log.error("SQLIntegrityConstraintViolationException(): " + exceptionDto.toString());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public final ResponseEntity<?> handleHttpValidationException(HttpMessageNotReadableException exception) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
            .message(exception.getMessage())
            .errorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
            .build();
        log.error("HttpMessageNotReadableException(): " + exceptionDto.toString());
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }
}
