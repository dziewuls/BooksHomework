package com.example.homework.controller;

import com.example.homework.domain.BookFacade;
import com.example.homework.domain.dto.AuthorRatingDto;
import com.example.homework.domain.dto.BookDto;
import com.example.homework.infrastructure.DataSourceNotFoundException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class BookController {

    private BookFacade bookFacade;
    private final Logger logger = LoggerFactory.getLogger(BookController.class);

    @GetMapping("/book/{isbn}")
    public ResponseEntity getBookByIsbn(@PathVariable String isbn) {
        BookDto result = null;
        try {
            result = this.bookFacade.getBookByIsbn(isbn);
        } catch (DataSourceNotFoundException e) {
            this.logger.error(e.getMessage());
        }
        return (result != null) ?
                new ResponseEntity<>(result, HttpStatus.OK) :
                new ResponseEntity<>("Book not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/category/{categoryName}/books")
    public ResponseEntity getBooksByCategory(@PathVariable String categoryName) {
        List<BookDto> result = new ArrayList<>();
        try {
            result = this.bookFacade.getBooksByCategory(categoryName);
        } catch (DataSourceNotFoundException e) {
            this.logger.error(e.getMessage());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/rating")
    public ResponseEntity getAuthorsOrderedByRating() {
        List<AuthorRatingDto> result = new ArrayList<>();
        try {
            result = this.bookFacade.getAuthorsOrderedByRating();
        } catch (DataSourceNotFoundException e) {
            this.logger.error(e.getMessage());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
