package com.example.homework.infrastructure;

import com.example.homework.domain.port.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SourceExceptionTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private BookRepository bookRepository;

    @Before
    public void createBookRepository() {
        this.bookRepository = new BookFromFileRepository(new ObjectMapper());
    }

    @Test
    public void shouldThrowDataSourceNotFoundExceptionWhenPropertySourceIsNotProvided() throws DataSourceNotFoundException {
        System.clearProperty("source");
        this.exceptionRule.expect(DataSourceNotFoundException.class);
        this.exceptionRule.expectMessage("Data source not provided.");
        this.bookRepository.findByCategory("Science Fiction");
    }

    @Test
    public void shouldThrowDataSourceNotFoundExceptionWhenPropertySourceIsWrong() throws DataSourceNotFoundException {
        System.setProperty("source", "example");
        this.exceptionRule.expect(DataSourceNotFoundException.class);
        this.exceptionRule.expectMessage("Data source is incorrect.");
        this.bookRepository.findByCategory("Science Fiction");
    }
}
