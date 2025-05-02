package com.library.controller;

import com.library.model.BorrowRecord;
import com.library.service.BorrowRecordService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow-records")
public class BorrowRecordController {

    private final BorrowRecordService borrowRecordService;

    @Autowired
    public BorrowRecordController(BorrowRecordService borrowRecordService) {
        this.borrowRecordService = borrowRecordService;
    }

    // Borrow a book
    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(
            @RequestParam Long bookId,
            @RequestParam Long userId) {
        try {
            BorrowRecord record = borrowRecordService.borrowBook(bookId, userId);
            return new ResponseEntity<>(record, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Return a book
    @PostMapping("/return")
    public ResponseEntity<?> returnBook(@RequestParam Long borrowRecordId) {
        try {
            BorrowRecord record = borrowRecordService.returnBook(borrowRecordId);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get borrowing history for a user
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<BorrowRecord>> getBorrowHistoryByUser(@PathVariable Long userId) {
        List<BorrowRecord> history = borrowRecordService.getBorrowHistoryByUser(userId);
        return ResponseEntity.ok(history);
    }

    // Get all borrow records (for librarians)
    @GetMapping
    public ResponseEntity<List<BorrowRecord>> getAllBorrowRecords() {
        List<BorrowRecord> allRecords = borrowRecordService.getAllBorrowRecords();
        return ResponseEntity.ok(allRecords);
    }

    // Get all overdue records
    @GetMapping("/overdue")
    public ResponseEntity<List<BorrowRecord>> getOverdueRecords() {
        List<BorrowRecord> overdueRecords = borrowRecordService.getOverdueRecords();
        return ResponseEntity.ok(overdueRecords);
    }
}
