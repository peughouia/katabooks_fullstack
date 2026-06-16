package com.nickel.katabooks.order.utils;

import com.nickel.katabooks.order.dto.CheckoutRequest;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Component
public class CardValidator {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("MM/yy");

    public void validate(CheckoutRequest request) {
        verifyNumber(request.getCardNumber());
        verifyExpiration(request.getExpiryDate());
    }

    private void verifyNumber(String cardNumber) {
        if (cardNumber.equals("0000000000000000")) {
            throw new IllegalArgumentException("Carte bancaire refusée");
        }
    }

    private void verifyExpiration(String expiryDate) {
        YearMonth expiration = YearMonth.parse(expiryDate, FORMATTER);
        if (expiration.isBefore(YearMonth.now())) {
            throw new IllegalArgumentException("Carte bancaire expirée");
        }
    }
}
