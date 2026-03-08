package cat.itacademy.s04.t02.n01.fruit_Api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDetails> handleValidationErrors (MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        ValidationErrorDetails details = new ValidationErrorDetails(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Fail",
                errors
        );

        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);

    }
    @ExceptionHandler(FruitNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleNotFound(FruitNotFoundException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FruitAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> handleAlreadyExist(FruitAlreadyExistsException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGeneralError(Exception ex) {
        return buildResponse("An unexpected internal error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorDetails> buildResponse(String message, HttpStatus status) {
        ErrorDetails details = new ErrorDetails(LocalDateTime.now(), status.value(), message);
        return new ResponseEntity<>(details, status);
    }

    public record ErrorDetails(LocalDateTime timestamp, int status, String message) {}



    public record ValidationErrorDetails(LocalDateTime timestamp, int status, String message, Map<String, String> errors) {}

}
