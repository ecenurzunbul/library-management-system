package com.library.dto;

import java.time.LocalDate;

import com.library.model.Book;
import com.library.model.BorrowRecord;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BookDTO {

    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Author is required")
    private String author;
    
    @NotBlank(message = "ISBN is required")
    @Size(min = 10, max = 13, message = "ISBN must be 10 to 13 characters")
    private String isbn;
    
    private LocalDate publicationDate;
    
    @NotBlank(message = "Genre is required")
    private String genre;

    public static BookDTO toDTO(Book record) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setAuthor(record.getAuthor());
        bookDTO.setIsbn(record.getIsbn());
        bookDTO.setPublicationDate(record.getPublicationDate());
        bookDTO.setTitle(record.getTitle());
        bookDTO.setGenre(record.getGenre());
        return bookDTO;
    }
}
