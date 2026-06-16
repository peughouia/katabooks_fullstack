package com.nickel.katabooks.book;

import com.nickel.katabooks.Exception.BookNotFoundException;
import com.nickel.katabooks.book.dto.BookCatalogResponseDto;
import com.nickel.katabooks.book.dto.BookDetailResponseDto;
import com.nickel.katabooks.book.dto.BookMapper;
import com.nickel.katabooks.order.repository.OrderItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    private static final int PAGE_SIZE = 10;

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final OrderItemRepository orderItemRepository;

    public BookService(BookRepository bookRepository, BookMapper bookMapper, OrderItemRepository orderItemRepository){
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.orderItemRepository = orderItemRepository;
    }

    public Page<BookCatalogResponseDto> getCatalog(int pageNumber){
        PageRequest pageRequest = PageRequest.of(pageNumber, PAGE_SIZE);
        return bookRepository.findAll(pageRequest)
                .map(bookMapper::toCatalogResponse);
    }

    public BookDetailResponseDto getBookDetail(Long bookId){
        Book book = foundBook(bookId);
        return bookMapper.toDetailResponse(book);
    }

    private Book foundBook(Long bookId){
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
    }

}
