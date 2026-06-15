package com.nickel.katabooks.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookDetailResponseDto {
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String summary;
    private String description;
    private String imageUrl;
    private Integer numberOfPages;
    private Double price;
    private Integer stock;
}
