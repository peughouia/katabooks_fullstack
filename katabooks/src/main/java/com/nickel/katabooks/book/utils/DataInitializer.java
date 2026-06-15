package com.nickel.katabooks.book.utils;

import com.nickel.katabooks.book.Book;
import com.nickel.katabooks.book.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DataInitializer {

    @Bean
    @Profile("!test")
    public CommandLineRunner initBooks(BookRepository bookRepository){
        return args -> {
            if (bookRepository.count() == 0){
                for (int i = 1; i<=25; i++){
                    Book book = new Book();
                    book.setTitle("Livre " + i);
                    book.setAuthor("Auteur " + i);
                    book.setSummary("Résumé court du livre " + i);
                    book.setDescription("Description longue et détaillée du livre " + i);
                    book.setImageUrl("https://picsum.photos/seed/" + i + "/200/300");
                    book.setNumberOfPages(100 + i * 10);
                    book.setStock(5 + i);
                    book.setPrice(9.99 + i);
                    book.setIsbn(IsbnGenerator.generateFakeIsbn13());
                    bookRepository.save(book);
                }
                System.out.println("25 livres inséré en base");
            }
        };
    }
}
