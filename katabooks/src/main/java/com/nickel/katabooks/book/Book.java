package com.nickel.katabooks.book;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 13)
    private String isbn;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false)
    private String author;

    @NotBlank
    @Column(nullable = false, length = 500)
    private String summary;

    @Column
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String description; // description longue

    @Min(1)
    @Column
    private Integer numberOfPages;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer stock = 0;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Double price = 0.0;

}
