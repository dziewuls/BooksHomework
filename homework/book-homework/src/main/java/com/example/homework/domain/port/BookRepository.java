package com.example.homework.domain.port;

import com.example.homework.domain.model.Book;
import com.example.homework.infrastructure.DataSourceNotFoundException;

import java.util.List;

public interface BookRepository {
    List<Book> findAll() throws DataSourceNotFoundException;

    Book findByISBN(String isbn) throws DataSourceNotFoundException;

    List<Book> findByCategory(String categoryName) throws DataSourceNotFoundException;
}
