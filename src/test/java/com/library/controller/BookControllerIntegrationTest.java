package com.library.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dto.BookDTO;
import com.library.model.Book;
import com.library.repository.BookRepository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.*;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Transactional  // rollback changes after each test
class BookControllerIntegrationTest {


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private BookRepository bookRepository;


    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void addBook_ShouldReturnCreatedBook() throws Exception {
        BookDTO dto = new BookDTO();
        dto.setTitle("Integration Test Book");
        dto.setAuthor("Integration Author");
        dto.setIsbn("1234567890123");
        dto.setPublicationDate(LocalDate.of(2021, 5, 10));
        dto.setGenre("Test");


        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Integration Test Book"))
                .andExpect(jsonPath("$.author").value("Integration Author"));
    }


    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void getBookById_ShouldReturnBook() throws Exception {
        // Prepare data first
        Book book = new Book();
        book.setTitle("Sample");
        book.setAuthor("Author");
        book.setIsbn("1234567890");
        book.setAvailable(true);
        book.setGenre("Fiction");
        book.setPublicationDate(LocalDate.of(2020, 1, 1));
        book = bookRepository.save(book);


        mockMvc.perform(get("/api/books/{id}", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Sample"))
                .andExpect(jsonPath("$.author").value("Author"));
    }


    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void getBookById_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/books/{id}", 999L))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void searchBooks_ShouldReturnBooks() throws Exception {
        // Prepare data first
        Book book1 = new Book();
        book1.setTitle("Test Book 1");
        book1.setAuthor("Author 1");
        book1.setIsbn("1234567890");
        book1.setAvailable(true);
        book1.setGenre("Fiction");
        book1.setPublicationDate(LocalDate.of(2020, 1, 1));
        bookRepository.save(book1);


        Book book2 = new Book();
        book2.setTitle("Test Book 2");
        book2.setAuthor("Author 2");
        book2.setIsbn("0987654321");
        book2.setAvailable(true);
        book2.setGenre("Non-Fiction");
        book2.setPublicationDate(LocalDate.of(2020, 2, 1));
        bookRepository.save(book2);


        mockMvc.perform(get("/api/books")
                        .param("title", "Test")
                        .param("author", "Author")
                        .param("genre", "Fiction")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Book 1"))
                .andExpect(jsonPath("$.content[1].title").value("Test Book 2"));




    }


}
