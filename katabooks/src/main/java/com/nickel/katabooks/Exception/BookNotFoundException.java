package com.nickel.katabooks.Exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long bookId) {
        super("Livre introuvable avec l'id : " + bookId);
    }
}
