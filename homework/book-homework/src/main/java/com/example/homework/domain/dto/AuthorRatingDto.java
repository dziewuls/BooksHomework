package com.example.homework.domain.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AuthorRatingDto {
    private String author;
    private double averageRating;
}
