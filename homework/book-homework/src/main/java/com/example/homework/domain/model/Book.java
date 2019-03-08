package com.example.homework.domain.model;

import com.example.homework.domain.dto.BookDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Book {
    private String isbn;
    private String title;
    private String subtitle;
    private String publisher;
    private Long publishedDate;
    private String description;
    private Integer pageCount;
    private String thumbnailUrl;
    private String language;
    private String previewLink;
    private Double averageRating;
    private List<String> authors;
    private List<String> categories;

    public BookDto toDto() {
        BookDto result = new BookDto();

        result.setIsbn(this.isbn);
        result.setTitle(this.title);
        result.setSubtitle(this.subtitle);
        result.setPublisher(this.publisher);
        result.setPublishedDate(this.publishedDate);
        result.setDescription(this.description);
        result.setPageCount(this.pageCount);
        result.setThumbnailUrl(this.thumbnailUrl);
        result.setLanguage(this.language);
        result.setPreviewLink(this.previewLink);
        result.setAverageRating(this.averageRating);
        result.setAuthors(this.authors);
        result.setCategories(this.categories);

        return result;
    }
}
