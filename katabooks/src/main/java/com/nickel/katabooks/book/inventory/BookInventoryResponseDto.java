package com.nickel.katabooks.book.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookInventoryResponseDto {
    private Long bookId;
    private String title;
    private String author;
    private Integer stockDisponible;
    private Integer nombreVendus;
}
