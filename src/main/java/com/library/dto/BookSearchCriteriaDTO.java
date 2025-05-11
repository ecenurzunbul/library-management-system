package com.library.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookSearchCriteriaDTO {
    private String title;
    private String author;
    private String isbn;
    private String genre;

}
