package com.library.controller;

import com.library.dto.BookDTO;
import com.library.model.Book;
import com.library.service.BookService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RestController
@RequestMapping("/api/books")
@Validated
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService){
        this.bookService = bookService;
    }

    // Add new book
    @PostMapping
    public ResponseEntity<Book> addBook(@Valid @RequestBody BookDTO bookDTO){
        Book newBook = bookService.addBook(bookDTO);
        return new ResponseEntity<>(newBook, HttpStatus.CREATED);
    }

    // Get book details by id
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id){
        Optional<Book> bookOpt = bookService.getBookById(id);
        if(bookOpt.isPresent()){
            BookDTO bookDTO = new BookDTO();
            bookDTO.setAuthor(bookOpt.get().getAuthor());
            bookDTO.setIsbn(bookOpt.get().getIsbn());
            bookDTO.setPublicationDate(bookOpt.get().getPublicationDate());
            bookDTO.setTitle(bookOpt.get().getTitle());
            bookDTO.setGenre(bookOpt.get().getGenre());
            return new ResponseEntity<>(bookDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Search books with pagination and optional filters (query params)
    @GetMapping
    public ResponseEntity<Page<BookDTO>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String genre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookService.searchBooks(title, author, isbn, genre, pageable);

        Page<BookDTO> bookDTOS = books.map(book -> BookDTO.builder()
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .publicationDate(book.getPublicationDate())
                .genre(book.getGenre())
                .build());

        return ResponseEntity.ok(bookDTOS);
    }

    // Update book info
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id,
                                           @Valid @RequestBody BookDTO bookDTO){
        try {
            Book updatedBook = bookService.updateBook(id, bookDTO);
            return ResponseEntity.ok(updatedBook);
        } catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a book
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id){
        try{
            bookService.deleteBook(id);
            return ResponseEntity.noContent().build();
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }
}
