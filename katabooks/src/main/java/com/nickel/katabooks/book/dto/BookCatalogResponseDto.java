package com.nickel.katabooks.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookCatalogResponseDto {
    private Long id;
    private String title;
    private String author;
    private String summary;
    private String imageUrl;
    private Double price;
}
