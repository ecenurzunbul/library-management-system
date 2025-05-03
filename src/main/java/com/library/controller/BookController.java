package com.library.controller;

import com.library.dto.BookDTO;
import com.library.model.Book;
import com.library.service.BookService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@Validated
@Slf4j
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // Add new book
    @PreAuthorize("hasRole('LIBRARIAN')")
    @PostMapping
    public ResponseEntity<BookDTO> addBook(@Valid @RequestBody BookDTO bookDTO) {
        Book newBook = bookService.addBook(bookDTO);
        return new ResponseEntity<>(BookDTO.toDTO(newBook), HttpStatus.CREATED);
    }

    // Get book details by id
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('PATRON')")
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        Optional<Book> bookOpt = bookService.getBookById(id);
        if (bookOpt.isPresent()) {
            BookDTO bookDTO = BookDTO.toDTO(bookOpt.get());
            return new ResponseEntity<>(bookDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Search books with pagination and optional filters (query params)
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('PATRON')")
    @GetMapping
    public ResponseEntity<Page<BookDTO>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String genre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookService.searchBooks(title, author, isbn, genre, pageable);

        Page<BookDTO> bookDTOS = books.map(BookDTO::toDTO);

        return ResponseEntity.ok(bookDTOS);
    }

    // Update book info
    @PreAuthorize("hasRole('LIBRARIAN')")
    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id,
                                              @Valid @RequestBody BookDTO bookDTO) {
        try {
            Book updatedBook = bookService.updateBook(id, bookDTO);
            return ResponseEntity.ok(BookDTO.toDTO(updatedBook));
        } catch (RuntimeException e) {
            log.error("Error updating book: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a book
    @PreAuthorize("hasRole('LIBRARIAN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting book: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
