package com.example.homework.domain;

import com.example.homework.domain.dto.AuthorRatingDto;
import com.example.homework.domain.dto.BookDto;
import com.example.homework.domain.model.Book;
import com.example.homework.domain.port.BookRepository;
import com.example.homework.infrastructure.DataSourceNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class BookFacadeTest {

    @Mock
    private BookRepository bookRepository;

    private BookFacade bookFacade;
    private List<Book> booksFromRepository = new ArrayList<>();

    @Before
    public void createBookFacade() {
        this.bookFacade = new BookFacade(this.bookRepository);
    }

    @Before
    public void createBooksFromRepository() {
        Book firstExampleBook = new Book();
        firstExampleBook.setTitle("First Example");
        firstExampleBook.setIsbn("1111");
        firstExampleBook.setAuthors(new ArrayList<>(Arrays.asList("Leonardo da Vinci", "Miachael Angelo")));
        firstExampleBook.setCategories(new ArrayList<>(Arrays.asList("Computers", "Java")));
        firstExampleBook.setAverageRating(5.0);

        Book secondExampleBook = new Book();
        secondExampleBook.setTitle("Second Example");
        secondExampleBook.setIsbn("2222");
        secondExampleBook.setAuthors(new ArrayList<>(Arrays.asList("Leonardo da Vinci", "Ludwig van Beethoven")));
        secondExampleBook.setCategories(new ArrayList<>(Arrays.asList("Computers")));
        secondExampleBook.setAverageRating(4.5);

        Book thirdExampleBook = new Book();
        thirdExampleBook.setTitle("Third Example");
        thirdExampleBook.setIsbn("3333");
        thirdExampleBook.setAuthors(new ArrayList<>(Arrays.asList("Robert D'Artois", "Leonardo da Vinci")));
        thirdExampleBook.setCategories(new ArrayList<>(Arrays.asList("France")));
        thirdExampleBook.setAverageRating(0.0);

        this.booksFromRepository.add(firstExampleBook);
        this.booksFromRepository.add(secondExampleBook);
        this.booksFromRepository.add(thirdExampleBook);
    }

    @Test
    public void shouldReturnBookDtoByGivenIsbn() throws DataSourceNotFoundException {
        //given
        Mockito.when(this.bookRepository.findByISBN("1111")).thenReturn(this.booksFromRepository.get(0));
        BookDto expectedBook = new BookDto();
        expectedBook.setTitle("First Example");
        expectedBook.setIsbn("1111");
        expectedBook.setAuthors(new ArrayList<>(Arrays.asList("Leonardo da Vinci", "Miachael Angelo")));
        expectedBook.setCategories(new ArrayList<>(Arrays.asList("Computers", "Java")));
        expectedBook.setAverageRating(5.0);

        //when
        BookDto result = this.bookFacade.getBookByIsbn("1111");

        //then
        assertThat(result).isEqualTo(expectedBook);
    }

    @Test
    public void shouldReturnNullWhenIsbnIsNotFound() throws DataSourceNotFoundException {
        //given
        Mockito.when(this.bookRepository.findByISBN("4444")).thenReturn(null);

        //when
        BookDto result = this.bookFacade.getBookByIsbn("4444");

        //then
        assertThat(result).isNull();
    }

    @Test
    public void shouldReturnListOfBooksDtoByCategory() throws DataSourceNotFoundException {
        //given
        Mockito.when(this.bookRepository.findByCategory("Computers"))
                .thenReturn(new ArrayList<>(Arrays.asList(this.booksFromRepository.get(0), this.booksFromRepository.get(1))));

        BookDto firstExpectedBook = new BookDto();
        firstExpectedBook.setTitle("First Example");
        firstExpectedBook.setIsbn("1111");
        firstExpectedBook.setAuthors(new ArrayList<>(Arrays.asList("Leonardo da Vinci", "Miachael Angelo")));
        firstExpectedBook.setCategories(new ArrayList<>(Arrays.asList("Computers", "Java")));
        firstExpectedBook.setAverageRating(5.0);

        BookDto secondExpectedBook = new BookDto();
        secondExpectedBook.setTitle("Second Example");
        secondExpectedBook.setIsbn("2222");
        secondExpectedBook.setAuthors(new ArrayList<>(Arrays.asList("Leonardo da Vinci", "Ludwig van Beethoven")));
        secondExpectedBook.setCategories(new ArrayList<>(Collections.singletonList("Computers")));
        secondExpectedBook.setAverageRating(4.5);

        List<BookDto> expectedList = new ArrayList<>(Arrays.asList(firstExpectedBook, secondExpectedBook));

        //when
        List<BookDto> result = this.bookFacade.getBooksByCategory("Computers");

        //then
        assertThat(result).containsExactlyInAnyOrderElementsOf(expectedList);
    }

    @Test
    public void shouldReturnEmptyListWhenCategoryIsNotFound() throws DataSourceNotFoundException {
        //given
        Mockito.when(this.bookRepository.findByCategory("Fantasy")).thenReturn(new ArrayList<>());

        //when
        List<BookDto> result = this.bookFacade.getBooksByCategory("Fantasy");

        //then
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void shouldReturnListOfAuthorsRatingsOrderedByAverageRating() throws DataSourceNotFoundException {
        //given
        Mockito.when(this.bookRepository.findAll()).thenReturn(this.booksFromRepository);
        List<AuthorRatingDto> expectedRatings = new ArrayList<>(Arrays.asList(
                new AuthorRatingDto("Miachael Angelo", 5),
                new AuthorRatingDto("Leonardo da Vinci", 4.75),
                new AuthorRatingDto("Ludwig van Beethoven", 4.5)
        ));

        //when
        List<AuthorRatingDto> result = this.bookFacade.getAuthorsOrderedByRating();

        //then
        assertThat(result).containsExactlyInAnyOrderElementsOf(expectedRatings);
    }
}