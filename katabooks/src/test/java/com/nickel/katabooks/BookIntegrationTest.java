package com.nickel.katabooks;

import com.nickel.katabooks.book.Book;
import com.nickel.katabooks.book.BookRepository;
import com.nickel.katabooks.book.utils.IsbnGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BookIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp(){
        //initialisation des données
        bookRepository.deleteAll();
        for (int i = 1; i <= 15; i++) {
            Book book = new Book();
            book.setTitle("Livre " + i);
            book.setAuthor("Auteur " + i);
            book.setSummary("Résumé " + i);
            book.setDescription("Description " + i);
            book.setNumberOfPages(100 + i);
            book.setStock(5);
            book.setPrice(9.99);
            book.setIsbn(IsbnGenerator.generateFakeIsbn13());
            bookRepository.save(book);
        }
    }

    @Test
    void shouldReturn200WithTenBooksOnFirstPage() throws Exception {
        mockMvc.perform(get("/books?page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.content[0].title").value("Livre 1"));
    }

    @Test
    void shouldReturn200WithRemainingBooksOnSecondPage() throws Exception {
        mockMvc.perform(get("/books?page=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5));
    }

    @Test
    void shouldNotExposeDescriptionInCatalog() throws Exception {
        mockMvc.perform(get("/books?page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].description").doesNotExist());
    }

    @Test
    void shouldReturn200WithFullBookDetail() throws Exception {
        Long bookId = bookRepository.findAll().getFirst().getId();

        mockMvc.perform(get("/books/" + bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.isbn").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.numberOfPages").exists())
                .andExpect(jsonPath("$.stock").exists());
    }

    @Test
    void shouldReturn404WhenBookDoesNotExist() throws Exception {
        mockMvc.perform(get("/books/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }
}
