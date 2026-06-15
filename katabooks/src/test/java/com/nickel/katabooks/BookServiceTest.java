package com.nickel.katabooks;


import com.nickel.katabooks.Exception.BookNotFoundException;
import com.nickel.katabooks.book.Book;
import com.nickel.katabooks.book.BookRepository;
import com.nickel.katabooks.book.BookService;
import com.nickel.katabooks.book.dto.BookCatalogResponseDto;
import com.nickel.katabooks.book.dto.BookDetailResponseDto;
import com.nickel.katabooks.book.dto.BookMapper;
import com.nickel.katabooks.book.utils.IsbnGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    private Book CreateBookTest(Long id, String title){
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor("Auteur Test");
        book.setSummary("Résumé test");
        book.setDescription("Description test");
        book.setNumberOfPages(200);
        book.setStock(5);
        book.setPrice(14.99);
        book.setIsbn(IsbnGenerator.generateFakeIsbn13());
        return book;
    }

    @Test
    void shouldReturnPageOfBookCatalog() {
        Book book1 = CreateBookTest(1L, "Livre A");
        Book book2 = CreateBookTest(2L, "Livre B");
        Page<Book> bookPage = new PageImpl<>(List.of(book1, book2));

        when(bookRepository.findAll(any(PageRequest.class))).thenReturn(bookPage);
        when(bookMapper.toCatalogResponse(book1))
                .thenReturn(new BookCatalogResponseDto(1L, "Livre A", "Auteur Test", "Résumé test", null, 14.99));
        when(bookMapper.toCatalogResponse(book2))
                .thenReturn(new BookCatalogResponseDto(2L, "Livre B", "Auteur Test", "Résumé test", null, 14.99));

        Page<BookCatalogResponseDto> result = bookService.getCatalog(0);

        assertEquals(2, result.getContent().size());
        assertEquals("Livre A", result.getContent().getFirst().getTitle());
        verify(bookRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void shouldReturnEmptyPageWhenNoBooksExist() {
        when(bookRepository.findAll(any(PageRequest.class))).thenReturn(Page.empty());

        Page<BookCatalogResponseDto> result = bookService.getCatalog(0);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnBookDetailWhenBookExists() {
        Book book = CreateBookTest(1L, "Livre A");

        BookDetailResponseDto detailResponse = new BookDetailResponseDto(
                1L, "9786492887435", "Livre A", "Auteur Test",
                "Résumé test", "Description test",
                "https://picsum.photos/seed/2/200/300", 200, 14.99, 5
        );

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toDetailResponse(book)).thenReturn(detailResponse);

        BookDetailResponseDto result = bookService.getBookDetail(1L);

        assertNotNull(result);
        assertEquals("Livre A", result.getTitle());
        assertEquals(200, result.getNumberOfPages());
    }

    @Test
    void shouldThrowBookNotFoundExceptionWhenBookDoesNotExist() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> bookService.getBookDetail(99L));
    }
}
