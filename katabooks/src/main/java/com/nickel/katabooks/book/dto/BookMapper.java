package com.nickel.katabooks.book.dto;

import com.nickel.katabooks.book.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public BookCatalogResponseDto toCatalogResponse(Book book){
        return new BookCatalogResponseDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getSummary(),
                book.getImageUrl(),
                book.getPrice()
        );
    }

    public BookDetailResponseDto toDetailResponse(Book book){
        return new BookDetailResponseDto(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getSummary(),
                book.getDescription(),
                book.getImageUrl(),
                book.getNumberOfPages(),
                book.getPrice(),
                book.getStock()
        );
    }
}
