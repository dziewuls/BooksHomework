package com.example.homework;

import com.example.homework.domain.BookFacade;
import com.example.homework.domain.port.BookRepository;
import com.example.homework.infrastructure.BookFromFileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    BookFacade bookFacade(BookRepository bookRepository) {
        return new BookFacade(bookRepository);
    }

    @Bean
    BookRepository bookRepository() {
        return new BookFromFileRepository(new ObjectMapper());
    }
}

