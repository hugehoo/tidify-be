package tidify.tidify.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
