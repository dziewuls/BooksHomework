package com.example.homework.controller;

import com.example.homework.domain.BookFacade;
import com.example.homework.domain.dto.BookDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookFacade bookFacade;

    private List<BookDto> booksDtoToResponse = new ArrayList<>();

    @Before
    public void createBooksDtoToResponse() {
        BookDto firstExampleBook = new BookDto();
        firstExampleBook.setTitle("First Example");
        firstExampleBook.setIsbn("1111");
        firstExampleBook.setAuthors(new ArrayList<>(Arrays.asList("Leonardo da Vinci", "Miachael Angelo")));
        firstExampleBook.setCategories(new ArrayList<>(Arrays.asList("Computers", "Java")));
        firstExampleBook.setAverageRating(5.0);

        BookDto secondExampleBook = new BookDto();
        secondExampleBook.setTitle("Second Example");
        secondExampleBook.setIsbn("2222");
        secondExampleBook.setCategories(new ArrayList<>(Arrays.asList("Computers")));
        secondExampleBook.setAverageRating(4.5);

        this.booksDtoToResponse.add(firstExampleBook);
        this.booksDtoToResponse.add(secondExampleBook);
    }

    @Test
    public void shouldReturnStatusOkAndOnlyExistingFieldsWhenBookExist() throws Exception {
        when(this.bookFacade.getBookByIsbn("1111")).thenReturn(this.booksDtoToResponse.get(0));
        this.mockMvc
                .perform(get("/api/book/1111"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"isbn\":\"1111\",\"title\":\"First Example\",\"averageRating\":5.0,\"authors\":[\"Leonardo da Vinci\",\"Miachael Angelo\"],\"categories\":[\"Computers\",\"Java\"]}"));
    }

    @Test
    public void shouldReturnStatusNotFoundWhenBookNotExist() throws Exception {
        when(this.bookFacade.getBookByIsbn("4444")).thenReturn(null);
        this.mockMvc
                .perform(get("/api/book/4444"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book not found"));
    }

    @Test
    public void shouldReturnEmptyArrayWhenCategoryNotExist() throws Exception {
        when(this.bookFacade.getBooksByCategory("example")).thenReturn(new ArrayList<>());
        this.mockMvc
                .perform(get("/api/category/example/books"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void shouldReturnJsonWithOnlyExistingAndNotEmptyFields() throws Exception {
        when(this.bookFacade.getBooksByCategory("Computers")).thenReturn(this.booksDtoToResponse);
        this.mockMvc
                .perform(get("/api/category/Computers/books"))
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"isbn\":\"1111\",\"title\":\"First Example\",\"averageRating\":5.0,\"authors\":[\"Leonardo da Vinci\",\"Miachael Angelo\"],\"categories\":[\"Computers\",\"Java\"]},{\"isbn\":\"2222\",\"title\":\"Second Example\",\"averageRating\":4.5,\"categories\":[\"Computers\"]}]"));
    }
}