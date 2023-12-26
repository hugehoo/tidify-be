package tidify.tidify.exception;

import java.sql.SQLIntegrityConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ExceptionDto> handleResourceNotFoundException(ResourceNotFoundException exception) {
        ExceptionDto response = ExceptionDto.ofFailure(exception.getErrorCode(), exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<ExceptionDto> handleResourceNotFoundException(MethodArgumentNotValidException exception) {
        ExceptionDto response = ExceptionDto.ofFailure("400", exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public final ResponseEntity<ExceptionDto> handleSQLViolationException(SQLIntegrityConstraintViolationException exception) {
        ExceptionDto response = ExceptionDto.ofFailure(ErrorTypes.SQL_VIOLATION_EXCEPTION.getCode(),
            ErrorTypes.SQL_VIOLATION_EXCEPTION.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public final ResponseEntity<ExceptionDto> handleHttpValidationException(HttpMessageNotReadableException exception) {
        ExceptionDto response = ExceptionDto.ofFailure(Integer.toString(HttpStatus.BAD_REQUEST.value()),
            HttpStatus.BAD_REQUEST.name());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity<ExceptionDto> illegalArgumentException(IllegalArgumentException exception) {
        ExceptionDto response = ExceptionDto.ofFailure(Integer.toString(HttpStatus.BAD_REQUEST.value()),
            exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    // @ExceptionHandler(LogoutException.class)
    // public final ResponseEntity<?> logout(LogoutException exception) {
    //     // ExceptionDto exceptionDto = ExceptionDto.builder()
    //     //     .message(exception.getMessage())
    //     //     .errorCode("LOGOUT")
    //     //     .build();
    //     // log.error("HttpMessageNotReadableException(): " + exceptionDto.toString());
    //     return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    // }
}
