package com.library.service.impl;

import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.User;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRecordRepository;
import com.library.repository.UserRepository;
import com.library.service.BorrowRecordService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.library.constants.ErrorCode.*;

@Service
@Transactional
public class BorrowRecordServiceImpl implements BorrowRecordService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Value("${app.user.maximum-books}")
    private long maxBooksPerUser = 5;

    @Autowired
    public BorrowRecordServiceImpl(BorrowRecordRepository borrowRecordRepository,
                                   BookRepository bookRepository,
                                   UserRepository userRepository) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BorrowRecord borrowBook(Long bookId, Long userId) throws Exception {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (bookOpt.isEmpty()) {
            throw new Exception(BOOK_NOT_FOUND.getMessage());
        }
        if (userOpt.isEmpty()) {
            throw new Exception(USER_NOT_FOUND.getMessage());
        }

        Book book = bookOpt.get();

        if (!book.isAvailable()) {
            throw new Exception(BOOK_ALREADY_BORROWED.getMessage());
        }
        // Check if the user is eligible to borrow books
        List<BorrowRecord> userBorrowRecords = borrowRecordRepository.findByBookIdAndReturnedFalse(userId);
        if (userBorrowRecords.size() >= maxBooksPerUser) {
            throw new Exception(USER_MAXIMUM_BOOKS_BORROWED.getMessage());
        }

        BorrowRecord record = new BorrowRecord();
        record.setBook(book);
        record.setUser(userOpt.get());
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusWeeks(2)); // 2 weeks period

        // Update book availability to false
        book.setAvailable(false);
        bookRepository.save(book);

        return borrowRecordRepository.save(record);
    }

    @Override
    public BorrowRecord returnBook(Long borrowRecordId) throws Exception {
        Optional<BorrowRecord> recordOpt = borrowRecordRepository.findById(borrowRecordId);
        if (recordOpt.isEmpty()) {
            throw new Exception(BORROW_RECORD_NOT_FOUND.getMessage());
        }

        BorrowRecord record = recordOpt.get();

        if (record.isReturned()) {
            throw new Exception(BOOK_ALREADY_RETURNED.getMessage());
        }

        // Mark as returned
        record.setReturned(true);
        record.setReturnDate(LocalDate.now());

        // Update book availability to true
        Book book = record.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        return borrowRecordRepository.save(record);
    }

    @Override
    public List<BorrowRecord> getBorrowHistoryByUser(Long userId) {
        // Return all borrowing records for the user
        return borrowRecordRepository.findByUserId(userId);
    }

    @Override
    public List<BorrowRecord> getAllBorrowRecords() {
        return borrowRecordRepository.findAll();
    }

    @Override
    public List<BorrowRecord> getOverdueRecords() {
        LocalDate today = LocalDate.now();
        // Find records where dueDate is before today and not yet returned
        return borrowRecordRepository.findByDueDateBeforeAndReturnedFalse(today);
    }
}
