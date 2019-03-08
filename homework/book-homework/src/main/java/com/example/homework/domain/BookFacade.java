package com.example.homework.domain;

import com.example.homework.domain.dto.AuthorRatingDto;
import com.example.homework.domain.dto.BookDto;
import com.example.homework.domain.model.Book;
import com.example.homework.domain.port.BookRepository;
import com.example.homework.infrastructure.DataSourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class BookFacade {

    private BookRepository bookRepository;

    @Cacheable("isbn")
    public BookDto getBookByIsbn(String isbn) throws DataSourceNotFoundException {
        return Optional
                .ofNullable(this.bookRepository.findByISBN(isbn))
                .map(Book::toDto)
                .orElse(null);
    }

    @Cacheable("category")
    public List<BookDto> getBooksByCategory(String category) throws DataSourceNotFoundException {
        return this.bookRepository
                .findByCategory(category)
                .stream()
                .map(Book::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable("rating")
    public List<AuthorRatingDto> getAuthorsOrderedByRating() throws DataSourceNotFoundException {
        List<Book> books = this.bookRepository.findAll();
        Map<String, List<Double>> authorsRatings = new HashMap<>();
        List<AuthorRatingDto> result = new ArrayList<>();

        books.stream()
                .filter(b -> b.getAverageRating() != null && b.getAverageRating() != 0)
                .forEach(b -> this.addElementToRatingsMap(authorsRatings, b));

        authorsRatings.forEach((key, value) -> result.add(new AuthorRatingDto(key, this.calculateAverage(value))));
        result.sort(Comparator.comparing(AuthorRatingDto::getAverageRating).reversed());

        return result;
    }

    private Double calculateAverage(List<Double> ratings) {
        double result = 0;
        for (double rating : ratings) {
            result += rating;
        }
        result = (result / ratings.size()) * 100.00;
        return Math.round(result) / 100.00;
    }

    private void addElementToRatingsMap(Map<String, List<Double>> authorsRatings, Book b) {
        for (String author : b.getAuthors()) {
            if (authorsRatings.containsKey(author)) {
                authorsRatings.get(author).add(b.getAverageRating());
            } else {
                authorsRatings.put(author, new ArrayList<>(Collections.singletonList(b.getAverageRating())));
            }
        }
    }
}
