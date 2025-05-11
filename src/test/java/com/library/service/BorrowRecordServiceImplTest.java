package com.library.service;


import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.User;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRecordRepository;
import com.library.repository.UserRepository;
import com.library.service.impl.BorrowRecordServiceImpl;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;


import java.time.LocalDate;
import java.util.*;


import static com.library.constants.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class BorrowRecordServiceImplTest {


   @Mock
   private BorrowRecordRepository borrowRecordRepository;


   @Mock
   private BookRepository bookRepository;


   @Mock
   private UserRepository userRepository;


   private BorrowRecordServiceImpl borrowRecordService;


   @BeforeEach
   void setUp() {
       MockitoAnnotations.openMocks(this);
       borrowRecordService = new BorrowRecordServiceImpl(borrowRecordRepository, bookRepository, userRepository);
   }


   // borrowBook success
   @Test
   void borrowBook_ShouldReturnBorrowRecord_WhenBookAndUserAvailable() throws Exception {
       Long bookId = 1L;
       Long userId = 2L;


       Book book = new Book();
       book.setId(bookId);
       book.setAvailable(true);


       User user = new User();
       user.setId(userId);


       when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
       when(userRepository.findById(userId)).thenReturn(Optional.of(user));
       when(bookRepository.save(any(Book.class))).thenReturn(book);
       when(borrowRecordRepository.save(any(BorrowRecord.class))).thenAnswer(i -> i.getArgument(0));


       BorrowRecord record = borrowRecordService.borrowBook(bookId, userId);


       assertNotNull(record);
       assertEquals(book, record.getBook());
       assertEquals(user, record.getUser());
       assertFalse(book.isAvailable());
       assertFalse(record.isReturned());
       assertNotNull(record.getBorrowDate());
       assertNotNull(record.getDueDate());
       verify(bookRepository).save(book);
       verify(borrowRecordRepository).save(record);
   }


   // borrowBook - book not found
   @Test
   void borrowBook_ShouldThrowException_WhenBookNotFound() {
       when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());


       Exception ex = assertThrows(Exception.class, () -> borrowRecordService.borrowBook(1L, 1L));
       assertEquals("Book not found", ex.getMessage());
   }


   // borrowBook - user not found
   @Test
   void borrowBook_ShouldThrowException_WhenUserNotFound() {
       when(bookRepository.findById(anyLong())).thenReturn(Optional.of(new Book()));
       when(userRepository.findById(anyLong())).thenReturn(Optional.empty());


       Exception ex = assertThrows(Exception.class, () -> borrowRecordService.borrowBook(1L, 1L));
       assertEquals("User not found", ex.getMessage());
   }


   // borrowBook - book not available
   @Test
   void borrowBook_ShouldThrowException_WhenBookNotAvailable() {
       Book book = new Book();
       book.setAvailable(false);


       when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
       when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));


       Exception ex = assertThrows(Exception.class, () -> borrowRecordService.borrowBook(1L, 1L));
       assertEquals(BOOK_ALREADY_BORROWED.getMessage(), ex.getMessage());
   }


   // returnBook success
   @Test
   void returnBook_ShouldMarkReturnedAndUpdateBookAvailability() throws Exception {
       Long borrowId = 1L;
       Book book = new Book();
       book.setAvailable(false);


       BorrowRecord record = new BorrowRecord();
       record.setId(borrowId);
       record.setBook(book);
       record.setReturned(false);


       when(borrowRecordRepository.findById(borrowId)).thenReturn(Optional.of(record));
       when(bookRepository.save(any(Book.class))).thenReturn(book);
       when(borrowRecordRepository.save(any(BorrowRecord.class))).thenAnswer(i -> i.getArgument(0));


       BorrowRecord returnedRecord = borrowRecordService.returnBook(borrowId);


       assertTrue(returnedRecord.isReturned());
       assertEquals(book, returnedRecord.getBook());
       assertTrue(book.isAvailable());
       assertNotNull(returnedRecord.getReturnDate());


       verify(bookRepository).save(book);
       verify(borrowRecordRepository).save(returnedRecord);
   }


   // returnBook - record not found
   @Test
   void returnBook_ShouldThrowException_WhenRecordNotFound() {
       when(borrowRecordRepository.findById(anyLong())).thenReturn(Optional.empty());


       Exception ex = assertThrows(Exception.class, () -> borrowRecordService.returnBook(1L));
       assertEquals("Borrow record not found", ex.getMessage());
   }


   // returnBook - already returned
   @Test
   void returnBook_ShouldThrowException_WhenAlreadyReturned() {
       BorrowRecord record = new BorrowRecord();
       record.setReturned(true);

       when(borrowRecordRepository.findById(anyLong())).thenReturn(Optional.of(record));

       Exception ex = assertThrows(Exception.class, () -> borrowRecordService.returnBook(1L));
       assertEquals(BOOK_ALREADY_RETURNED.getMessage(), ex.getMessage());
   }


   // getBorrowHistoryByUser test
   @Test
   void getBorrowHistoryByUser_ShouldReturnList() {
       Long userId = 1L;
       List<BorrowRecord> records = List.of(new BorrowRecord(), new BorrowRecord());


       when(borrowRecordRepository.findByUserId(userId)).thenReturn(records);


       List<BorrowRecord> result = borrowRecordService.getBorrowHistoryByUser(userId);


       assertEquals(2, result.size());
       verify(borrowRecordRepository).findByUserId(userId);
   }


   // getAllBorrowRecords test
   @Test
   void getAllBorrowRecords_ShouldReturnAll() {
       List<BorrowRecord> records = List.of(new BorrowRecord(), new BorrowRecord());
       when(borrowRecordRepository.findAll()).thenReturn(records);


       List<BorrowRecord> result = borrowRecordService.getAllBorrowRecords();


       assertEquals(2, result.size());
       verify(borrowRecordRepository).findAll();
   }


   // getOverdueRecords test
   @Test
   void getOverdueRecords_ShouldReturnRecordsBeforeTodayAndNotReturned() {
       List<BorrowRecord> records = List.of(new BorrowRecord());
       when(borrowRecordRepository.findByDueDateBeforeAndReturnedFalse(LocalDate.now())).thenReturn(records);


       List<BorrowRecord> result = borrowRecordService.getOverdueRecords();


       assertEquals(1, result.size());
       verify(borrowRecordRepository).findByDueDateBeforeAndReturnedFalse(LocalDate.now());
   }

   @Test
    void borrowBook_shouldThrowException_WhenUserHasMaxBooks() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        List<BorrowRecord> userBorrowRecords = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            BorrowRecord record = new BorrowRecord();
            Book book = new Book();
            book.setId((long) i);
            book.setAvailable(false);
            record.setUser(user);
            record.setBook(book);
            record.setReturned(false);
            userBorrowRecords.add(record);
        }

        Book newBorrow = new Book();
        newBorrow.setId(1000L);
        newBorrow.setAvailable(true);

        when(bookRepository.findById(newBorrow.getId())).thenReturn(Optional.of(newBorrow));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(borrowRecordRepository.findByUserIdAndReturnedFalse(userId)).thenReturn(userBorrowRecords);

        Exception ex = assertThrows(Exception.class, () -> borrowRecordService.borrowBook(newBorrow.getId(), userId));
        assertEquals(USER_MAXIMUM_BOOKS_BORROWED.getMessage(), ex.getMessage());
    }

}