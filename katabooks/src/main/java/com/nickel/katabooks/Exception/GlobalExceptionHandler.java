package com.nickel.katabooks.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}


