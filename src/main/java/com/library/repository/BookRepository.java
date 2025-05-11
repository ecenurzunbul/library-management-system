package com.library.repository;

import com.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    Page<Book> findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndIsbnContainingAndGenreContainingIgnoreCase(String s, String s1, String s2, String s3, Pageable pageable);
}
