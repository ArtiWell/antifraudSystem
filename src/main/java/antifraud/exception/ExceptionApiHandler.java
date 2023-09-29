package antifraud.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionApiHandler {

    @ExceptionHandler({ConflictException.class})
    public ResponseEntity<Object> responseEntity(ConflictException e) {
        return new ResponseEntity<>(e.getCause(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({WrongDataException.class})
    public ResponseEntity<Object> responseEntity(WrongDataException e) {
        return new ResponseEntity<>(e.getCause(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<Object> responseEntity(EntityNotFoundException e) {
        return new ResponseEntity<>(e.getCause(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ViolationAuthorizationException.class})
    public ResponseEntity<Object> responseEntity(ViolationAuthorizationException e) {
        return new ResponseEntity<>(e.getCause(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({UnprocessableException.class})
    public ResponseEntity<Object> responseEntity(UnprocessableException e) {
        return new ResponseEntity<>(e.getCause(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> responseEntity(Exception e) {
        return new ResponseEntity<>(e.getCause(), HttpStatus.BAD_REQUEST);
    }

}
