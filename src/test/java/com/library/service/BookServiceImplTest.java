package com.library.service;


import com.library.dto.BookDTO;
import com.library.model.Book;
import com.library.repository.BookRepository;
import com.library.service.impl.BookServiceImpl;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;


import java.time.LocalDate;
import java.util.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class BookServiceImplTest {


   @Mock
   private BookRepository bookRepository;


   private BookService bookService;


   @BeforeEach
   void setUp() {
       MockitoAnnotations.openMocks(this);
       bookService = new BookServiceImpl(bookRepository);
   }


   @Test
   void addBook_ShouldReturnSavedBook() {
       BookDTO dto = new BookDTO();
       dto.setTitle("Test Book");
       dto.setAuthor("Test Author");
       dto.setIsbn("1234567890");
       dto.setPublicationDate(LocalDate.of(2022, 1, 1));
       dto.setGenre("Test Genre");


       Book savedBook = new Book();
       savedBook.setId(1L);
       savedBook.setTitle(dto.getTitle());
       savedBook.setAuthor(dto.getAuthor());
       savedBook.setIsbn(dto.getIsbn());
       savedBook.setPublicationDate(dto.getPublicationDate());
       savedBook.setGenre(dto.getGenre());
       savedBook.setAvailable(true);


       when(bookRepository.save(any(Book.class))).thenReturn(savedBook);


       Book result = bookService.addBook(dto);


       assertNotNull(result);
       assertEquals("Test Book", result.getTitle());
       assertTrue(result.isAvailable());


       verify(bookRepository, times(1)).save(any(Book.class));
   }


   @Test
   void getBookById_ShouldReturnBook_WhenFound() {
       Book book = new Book();
       book.setId(1L);
       book.setTitle("Test");


       when(bookRepository.findById(1L)).thenReturn(Optional.of(book));


       Optional<Book> result = bookService.getBookById(1L);


       assertTrue(result.isPresent());
       assertEquals("Test", result.get().getTitle());
   }


   // Add more tests: updateBook, deleteBook, searchBooks..


   @Test
   void searchBooks_ShouldReturnPageOfBooks() {
       Book book1 = new Book();
       book1.setId(1L);
       book1.setTitle("Test Book 1");
       book1.setAuthor("Author 1");


       Book book2 = new Book();
       book2.setId(2L);
       book2.setTitle("Test Book 2");
       book2.setAuthor("Author 2");


       List<Book> books = Arrays.asList(book1, book2);
       Page<Book> page = new PageImpl<>(books);


       when(bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndIsbnContainingAndGenreContainingIgnoreCase(
               anyString(), anyString(), anyString(), anyString(), any(Pageable.class)))
               .thenReturn(page);


       Page<Book> result = bookService.searchBooks(null, null, null, null, PageRequest.of(0, 10));


       assertNotNull(result);
       assertEquals(2, result.getTotalElements());
   }


   @Test
   void updateBook_ShouldReturnUpdatedBook() {
       BookDTO dto = new BookDTO();
       dto.setTitle("Updated Book");
       dto.setAuthor("Updated Author");
       dto.setIsbn("0987654321");
       dto.setPublicationDate(LocalDate.of(2023, 1, 1));
       dto.setGenre("Updated Genre");


       Book existingBook = new Book();
       existingBook.setId(1L);
       existingBook.setTitle("Old Title");


       when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
       when(bookRepository.save(any(Book.class))).thenReturn(existingBook);


       Book result = bookService.updateBook(1L, dto);


       assertNotNull(result);
       assertEquals("Updated Book", result.getTitle());
   }


   @Test
   void deleteBook_ShouldDeleteBook_WhenExists() {
       Long bookId = 1L;


       when(bookRepository.existsById(bookId)).thenReturn(true);


       bookService.deleteBook(bookId);


       verify(bookRepository, times(1)).deleteById(bookId);
   }


   @Test
   void deleteBook_ShouldThrowException_WhenNotExists() {
       Long bookId = 1L;


       when(bookRepository.existsById(bookId)).thenReturn(false);


       Exception exception = assertThrows(RuntimeException.class, () -> {
           bookService.deleteBook(bookId);
       });


       assertEquals("Book not found with id: " + bookId, exception.getMessage());
   }

    @Test
    void getBookById_ShouldReturnEmpty_WhenNotFound() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Book> result = bookService.getBookById(999L);

        assertFalse(result.isPresent());
    }
    @Test
    void updateBook_ShouldThrowException_WhenBookNotFound() {
        BookDTO dto = new BookDTO();
        dto.setTitle("Doesn't matter");

        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            bookService.updateBook(1L, dto);
        });

        assertEquals("Book not found with id: 1", exception.getMessage());
    }



}
