package com.library.service;

import com.library.model.BorrowRecord;
import com.library.model.User;
import com.library.model.Book;

import java.util.List;
import java.util.Optional;

public interface BorrowRecordService {
    BorrowRecord borrowBook(Long bookId, Long userId) throws Exception;
    
    BorrowRecord returnBook(Long borrowRecordId) throws Exception;
    
    List<BorrowRecord> getBorrowHistoryByUser(Long userId);
    
    List<BorrowRecord> getAllBorrowRecords();

    List<BorrowRecord> getAllBorrowRecords(Long userId);

    List<BorrowRecord> getOverdueRecords();
}