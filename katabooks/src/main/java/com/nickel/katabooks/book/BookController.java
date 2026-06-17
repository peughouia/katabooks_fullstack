package com.nickel.katabooks.book;

import com.nickel.katabooks.book.dto.BookCatalogResponseDto;
import com.nickel.katabooks.book.dto.BookDetailResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<Page<BookCatalogResponseDto>> getCatalog(
            @RequestParam(defaultValue = "0") int page){
        return ResponseEntity.ok(bookService.getCatalog(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDetailResponseDto> getBookDetail(@PathVariable Long id){
        return ResponseEntity.ok(bookService.getBookDetail(id));
    }
}

