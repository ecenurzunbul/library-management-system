package com.library.controller;

import com.library.dto.BorrowRecordDTO;
import com.library.model.BorrowRecord;
import com.library.service.BorrowRecordService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
            return new ResponseEntity<>( BorrowRecordDTO.toDTO(record), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Return a book
    @PostMapping("/return")
    public ResponseEntity<?> returnBook(@RequestParam Long borrowRecordId) {
        try {
            BorrowRecord record = borrowRecordService.returnBook(borrowRecordId);
            return ResponseEntity.ok(BorrowRecordDTO.toDTO(record));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get borrowing history for a user
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<BorrowRecordDTO>> getBorrowHistoryByUser(@PathVariable Long userId) {
        List<BorrowRecord> history = borrowRecordService.getBorrowHistoryByUser(userId);
        List<BorrowRecordDTO> historyDTO = new ArrayList<>();
        history.forEach(borrowRecord -> {
            historyDTO.add(BorrowRecordDTO.toDTO(borrowRecord));
        });
        return ResponseEntity.ok(historyDTO);
    }

    // Get all borrow records (for librarians)
    @GetMapping
    public ResponseEntity<List<BorrowRecordDTO>> getAllBorrowRecords() {
        List<BorrowRecord> allRecords = borrowRecordService.getAllBorrowRecords();
        List<BorrowRecordDTO> allDTO = new ArrayList<>();
        allRecords.forEach(borrowRecord -> {
            allDTO.add(BorrowRecordDTO.toDTO(borrowRecord));
        });
        return ResponseEntity.ok(allDTO);
    }

    // Get all overdue records
    @GetMapping("/overdue")
    public ResponseEntity<List<BorrowRecordDTO>> getOverdueRecords() {
        List<BorrowRecord> overdueRecords = borrowRecordService.getOverdueRecords();
        List<BorrowRecordDTO> overdueDTO = new ArrayList<>();
        overdueRecords.forEach(borrowRecord -> {
            overdueDTO.add(BorrowRecordDTO.toDTO(borrowRecord));
        });
        return ResponseEntity.ok(overdueDTO);
    }
}
