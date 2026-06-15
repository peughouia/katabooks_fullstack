package com.nickel.katabooks.book.utils;

import java.util.Random;

public class IsbnGenerator {

    private static final Random random = new Random();

    // Constructeur privé pour empêcher d'instancier cette classe utilitaire
    private IsbnGenerator() {}

    /**
     * Génère un faux ISBN-13 mathématiquement valide
     */
    public static String generateFakeIsbn13() {
        // Un ISBN-13 commence généralement par 978
        StringBuilder isbn = new StringBuilder("978");

        // On génère les 9 chiffres suivants au hasard
        for (int i = 0; i < 9; i++) {
            isbn.append(random.nextInt(10));
        }

        // On calcule le 13ème chiffre (la clé de contrôle officielle)
        int checkDigit = calculateIsbn13CheckDigit(isbn.toString());
        isbn.append(checkDigit);

        return isbn.toString();
    }

    /**
     * Algorithme officiel de vérification de la clé de contrôle ISBN-13
     */
    private static int calculateIsbn13CheckDigit(String twelveDigits) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(twelveDigits.charAt(i));
            // Multiplie par 1 pour les positions paires (0, 2, 4...) et 3 pour les impaires
            sum += (i % 2 == 0) ? digit : digit * 3;
        }

        int remainder = sum % 10;
        return remainder == 0 ? 0 : 10 - remainder;
    }
}