package com.library.service;

import com.library.dto.BookDTO;
import com.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookService {
    Book addBook(BookDTO bookDTO);
    
    Book updateBook(Long bookId, BookDTO bookDTO);
    
    void deleteBook(Long bookId);
    
    Optional<Book> getBookById(Long bookId);
    
    Page<Book> searchBooks(String title, String author, String isbn, String genre, Pageable pageable);
}
