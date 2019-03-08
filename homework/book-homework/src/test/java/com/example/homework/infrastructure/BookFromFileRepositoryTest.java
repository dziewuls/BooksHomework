package com.example.homework.infrastructure;

import com.example.homework.domain.model.Book;
import com.example.homework.domain.port.BookRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class BookFromFileRepositoryTest {

    @Mock
    private ObjectMapper mockObjectMapper;

    private BookRepository bookRepository;
    private JsonNode booksFromSource;

    @Before
    public void createBookRepository() {
        this.bookRepository = new BookFromFileRepository(this.mockObjectMapper);
    }

    @Before
    public void createBooksFromSource() throws IOException {
        String sampleJson = "{\"items\":[{\"id\":\"1\",\"volumeInfo\":{\"title\":\"First Example\",\"publisher\":\"example\",\"publishedDate\":\"2019\",\"industryIdentifiers\":[{\"type\":\"ISBN_13\",\"identifier\":\"1111\"}],\"categories\":[\"Computers\"],\"imageLinks\":{\"thumbnail\":\"thumbnail\"},\"language\":\"en\",\"previewLink\":\"link\"}},{\"id\":\"2\",\"volumeInfo\":{\"title\":\"Second Example\",\"authors\":[\"Clifford Geertz\"],\"publisher\":\"University of Chicago Press\",\"publishedDate\":\"1976-02-15\",\"description\":\"Written with a rare combination of analysis and speculation\",\"pageCount\":392,\"categories\":[\"Computers\"],\"averageRating\":4,\"ratingsCount\":4,\"imageLinks\":{\"thumbnail\":\"thumbnail\"},\"language\":\"en\",\"previewLink\":\"link\"}}]}";
        ObjectMapper objectMapper = new ObjectMapper();
        this.booksFromSource = objectMapper.readTree(sampleJson);
    }

    @Before
    public void configMockAndProperty() throws IOException {
        System.setProperty("source", "");
        Mockito.when(this.mockObjectMapper.readTree(Mockito.any(File.class))).thenReturn(this.booksFromSource);
    }

    @Test
    public void shouldReturnBookWithIsbn13WhenItExist() throws IOException {
        //given
        String expectedIsbn = "1111";

        //when
        List<Book> results = this.bookRepository.findAll();

        //then
        assertThat(results.get(0).getIsbn()).isEqualTo(expectedIsbn);
    }

    @Test
    public void shouldReturnBookWithIdWhenIsbn13NotExist() throws IOException {
        //given
        String expectedIsbn = "2";

        //when
        List<Book> results = this.bookRepository.findAll();

        //then
        assertThat(results.get(1).getIsbn()).isEqualTo(expectedIsbn);
    }

    @Test
    public void shouldReturnBookWithDateInUnixTimestampWhenGivenIsOnlyYear() throws IOException {
        //given
        Long expectedDate = 1546297200000L;

        //when
        List<Book> results = this.bookRepository.findAll();

        //then
        assertThat(results.get(0).getPublishedDate()).isEqualTo(expectedDate);
    }

    @Test
    public void shouldReturnBookWithDateInUnixTimestampWhenGivenIsFullDate() throws IOException {
        //given
        Long expectedDate = 193186800000L;

        //when
        List<Book> results = this.bookRepository.findAll();

        //then
        assertThat(results.get(1).getPublishedDate()).isEqualTo(expectedDate);
    }

    @Test
    public void shouldReturnBookWithOnlyExistingFieldsInJson() throws IOException {
        //given
        String expectedTitle = "First Example";
        String expectedPublisher = "example";
        String expectedThumbnailUrl = "thumbnail";
        String expectedLanguage = "en";
        String expectedPreviewLink = "link";
        List<String> expectedCategories = new ArrayList<>(Collections.singletonList("Computers"));

        //when
        List<Book> results = this.bookRepository.findAll();

        //then
        assertThat(results.get(0))
                .hasFieldOrPropertyWithValue("title", expectedTitle)
                .hasFieldOrPropertyWithValue("publisher", expectedPublisher)
                .hasFieldOrPropertyWithValue("thumbnailUrl", expectedThumbnailUrl)
                .hasFieldOrPropertyWithValue("language", expectedLanguage)
                .hasFieldOrPropertyWithValue("previewLink", expectedPreviewLink)
                .hasFieldOrPropertyWithValue("categories", expectedCategories)
                .hasFieldOrPropertyWithValue("authors", new ArrayList<>())
                .hasFieldOrPropertyWithValue("averageRating", null)
                .hasFieldOrPropertyWithValue("subtitle", null)
                .hasFieldOrPropertyWithValue("description", null)
                .hasFieldOrPropertyWithValue("pageCount", null);
    }

    @Test
    public void shouldReturnBookByIsbnWhenExist() throws IOException {
        //given
        String expectedIsbn = "1111";

        //when
        Book result = this.bookRepository.findByISBN("1111");

        //then
        assertThat(result.getIsbn()).isEqualTo(expectedIsbn);
    }

    @Test
    public void shouldReturnNullWhenBookByIsbnNotExist() throws IOException {
        //when
        Book result = this.bookRepository.findByISBN("4444");

        //then
        assertThat(result).isNull();
    }

    @Test
    public void shouldReturnListOfBooksByCategoryWhenExist() throws IOException {
        //given
        String expectedCategory = "Computers";

        //when
        List<Book> results = this.bookRepository.findByCategory("Computers");

        //then
        assertThat(results)
                .hasSize(2)
                .areExactly(2, new Condition<>(b -> b.getCategories().contains(expectedCategory), "Category"));
    }

    @Test
    public void shouldReturnEmptyArrayWhenCategoryNotExist() throws IOException {
        //when
        List<Book> results = this.bookRepository.findByCategory("Science Fiction");

        //then
        assertThat(results).isEmpty();
    }
}