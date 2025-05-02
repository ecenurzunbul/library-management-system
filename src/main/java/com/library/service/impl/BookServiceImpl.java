package com.library.service.impl;

import com.library.dto.BookDTO;
import com.library.model.Book;
import com.library.repository.BookRepository;
import com.library.service.BookService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.Optional;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    @Override
    public Book addBook(BookDTO bookDTO) {
        // Convert DTO to Entity
        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setIsbn(bookDTO.getIsbn());
        book.setPublicationDate(bookDTO.getPublicationDate());
        book.setGenre(bookDTO.getGenre());
        book.setAvailable(true); // new books are available by default

        // Validation can be enhanced here
        
        return bookRepository.save(book);
    }

    @Override
    public Book updateBook(Long bookId, BookDTO bookDTO) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);

        if(optionalBook.isEmpty()){
            throw new RuntimeException("No book found with id: " + bookId);
        }

        Book book = optionalBook.get();
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setIsbn(bookDTO.getIsbn());
        book.setPublicationDate(bookDTO.getPublicationDate());
        book.setGenre(bookDTO.getGenre());

        return bookRepository.save(book);
    }

    @Override
    public void deleteBook(Long bookId) {
        if(!bookRepository.existsById(bookId)){
            throw new RuntimeException("Book not found with id: "+ bookId);
        }
        bookRepository.deleteById(bookId);
    }

    @Override
    public Optional<Book> getBookById(Long bookId){
        return bookRepository.findById(bookId);
    }

    @Override
    public Page<Book> searchBooks(String title, String author, String isbn, String genre, Pageable pageable) {
        return bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndIsbnContainingAndGenreContainingIgnoreCase(
                title != null ? title : "",
                author != null ? author : "",
                isbn != null ? isbn : "",
                genre != null ? genre : "",
                pageable);
    }
}
