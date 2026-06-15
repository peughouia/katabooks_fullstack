package com.nickel.katabooks.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 2. Intercepte les erreurs de validation (@Valid, @Email, @NotBlank...)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> erreurs = new HashMap<>();

        // On parcourt toutes les erreurs trouvées par Spring
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String nomDuChamp = ((FieldError) error).getField();
            String messageDerreur = error.getDefaultMessage();
            erreurs.put(nomDuChamp, messageDerreur);
        });

        // On renvoie un dictionnaire JSON avec le statut 400
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erreurs);
    }

    // 3. Intercepte nos erreurs métier personnalisées (ex: "Un compte avec cet email existe déjà")
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBusinessExceptions(IllegalArgumentException ex) {
        Map<String, String> erreur = new HashMap<>();
        erreur.put("erreur", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erreur);
    }

    //
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleBookNotFound(BookNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildError(ex.getMessage()));
    }

//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(construireErreur(ex.getMessage()));
//    }

    private Map<String, Object> buildError(String message) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "message", message
        );
    }


}


