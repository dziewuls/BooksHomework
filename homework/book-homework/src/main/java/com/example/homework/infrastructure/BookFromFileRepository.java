package com.example.homework.infrastructure;

import com.example.homework.domain.model.Book;
import com.example.homework.domain.port.BookRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public class BookFromFileRepository implements BookRepository {

    private ObjectMapper objectMapper;

    @Override
    public List<Book> findAll() throws DataSourceNotFoundException {
        File fileToRead;
        try {
            fileToRead = new File(System.getProperty("source"));
        } catch (NullPointerException e) {
            throw new DataSourceNotFoundException("Data source not provided.");
        }

        JsonNode jsonElements;
        try {
            jsonElements = this.objectMapper.readTree(fileToRead).withArray("items");
        } catch (IOException e) {
            throw new DataSourceNotFoundException("Data source is incorrect.");
        }

        List<Book> result = new ArrayList<>();
        jsonElements.forEach(e -> result.add(this.builtBook(e)));
        return result;
    }

    @Override
    public Book findByISBN(String isbn) throws DataSourceNotFoundException {
        Optional<Book> result = this
                .findAll()
                .stream()
                .filter(b -> isbn.equals(b.getIsbn()))
                .findFirst();

        return result.orElse(null);
    }

    @Override
    public List<Book> findByCategory(String categoryName) throws DataSourceNotFoundException {
        return this.findAll()
                .stream()
                .filter(b -> b.getCategories().contains(categoryName))
                .collect(Collectors.toList());
    }

    private Book builtBook(JsonNode element) {
        Book result = new Book();
        JsonNode bookInfo = element.get("volumeInfo");

        result.setIsbn(this.getIsbn(element));
        result.setTitle(this.getTextValueIfExist(bookInfo, "title"));
        result.setSubtitle(this.getTextValueIfExist(bookInfo, "subtitle"));
        result.setPublisher(this.getTextValueIfExist(bookInfo, "publisher"));
        result.setPublishedDate(this.getDateValueInUnixTimestampIfExist(bookInfo));
        result.setDescription(this.getTextValueIfExist(bookInfo, "description"));
        result.setLanguage(this.getTextValueIfExist(bookInfo, "language"));
        result.setPreviewLink(this.getTextValueIfExist(bookInfo, "previewLink"));
        result.setPageCount(this.getPageCountIfExist(bookInfo));
        result.setThumbnailUrl(this.getThumbnailIfExist(bookInfo));
        result.setAverageRating(this.getAverageRatingIfExist(bookInfo));
        result.setAuthors(this.getArrayValuesIfExist(bookInfo, "authors"));
        result.setCategories(this.getArrayValuesIfExist(bookInfo, "categories"));

        return result;
    }

    private String getIsbn(JsonNode element) {
        JsonNode bookInfo = element.get("volumeInfo");
        String isbn = element.get("id").asText();
        JsonNode industry = bookInfo.withArray("industryIdentifiers");
        for (JsonNode industryElement : industry) {
            if ("ISBN_13".equals(industryElement.get("type").asText())) {
                isbn = industryElement.get("identifier").asText();
            }
        }
        return isbn;
    }

    private Long getDateValueInUnixTimestampIfExist(JsonNode bookInfo) {
        if (bookInfo.has("publishedDate")) {
            String dateAsString = bookInfo.get("publishedDate").asText();
            String[] splitDate = dateAsString.split("-");
            int year = Integer.parseInt(splitDate[0]);
            int month = (splitDate.length >= 2) ? Integer.parseInt(splitDate[1]) : 1;
            int day = (splitDate.length == 3) ? Integer.parseInt(splitDate[2]) : 1;
            LocalDateTime date = LocalDateTime.of(year, month, day, 0, 0);
            Timestamp timestamp = Timestamp.valueOf(date);
            return timestamp.getTime();
        } else
            return null;
    }

    private String getTextValueIfExist(JsonNode bookInfo, String valueName) {
        if (bookInfo.has(valueName)) {
            return bookInfo.get(valueName).asText();
        } else {
            return null;
        }
    }

    private Integer getPageCountIfExist(JsonNode bookInfo) {
        if (bookInfo.has("pageCount")) {
            return bookInfo.get("pageCount").asInt();
        } else {
            return null;
        }
    }

    private Double getAverageRatingIfExist(JsonNode bookInfo) {
        if (bookInfo.has("averageRating")) {
            return bookInfo.get("averageRating").asDouble();
        } else {
            return null;
        }
    }

    private List<String> getArrayValuesIfExist(JsonNode bookInfo, String valueName) {
        List<String> result = new ArrayList<>();
        if (bookInfo.has(valueName)) {
            JsonNode valuesAsText = bookInfo.withArray(valueName);
            for (JsonNode value : valuesAsText) {
                result.add(value.asText());
            }
        }
        return result;
    }

    private String getThumbnailIfExist(JsonNode bookInfo) {
        if (bookInfo.has("imageLinks") && bookInfo.get("imageLinks").has("thumbnail")) {
            return bookInfo.get("imageLinks").get("thumbnail").asText();
        } else {
            return null;
        }
    }
}