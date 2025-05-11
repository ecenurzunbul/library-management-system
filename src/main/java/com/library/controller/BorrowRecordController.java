package com.library.controller;

import com.library.dto.BorrowBookRequestDTO;
import com.library.dto.BorrowRecordDTO;
import com.library.dto.ReturnBookRequestDTO;
import com.library.model.BorrowRecord;
import com.library.security.JwtUtils;
import com.library.service.BorrowRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.library.constants.ErrorCode.NOT_BORROWED_BY_USER;

@RestController
@RequestMapping("/api/borrow-records")
@Slf4j
public class BorrowRecordController {

    private final BorrowRecordService borrowRecordService;

    @Autowired
    public BorrowRecordController(BorrowRecordService borrowRecordService) {
        this.borrowRecordService = borrowRecordService;
    }

    // Borrow a book
    @PreAuthorize("hasRole('LIBRARIAN')")
    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(
            @RequestBody BorrowBookRequestDTO borrowBookRequest) {
        return borrowBookRecord(borrowBookRequest.getBookId(), borrowBookRequest.getUserId());
    }


    @PreAuthorize("hasRole('PATRON') or hasRole('LIBRARIAN')")
    @PostMapping("/self-borrow")
    public ResponseEntity<?> getSelfBorrowRecords(@RequestBody BorrowBookRequestDTO borrowBookRequest) {
        return borrowBookRecord(borrowBookRequest.getBookId(), JwtUtils.getUserId());
    }

    private ResponseEntity<?> borrowBookRecord(Long bookId, Long userId) {
        try {
            BorrowRecord record = borrowRecordService.borrowBook(bookId, userId);
            return new ResponseEntity<>(BorrowRecordDTO.toDTO(record), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error borrowing book: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Return a book
    @PreAuthorize("hasRole('LIBRARIAN')")
    @PostMapping("/return")
    public ResponseEntity<?> returnBook(@RequestBody ReturnBookRequestDTO returnBookRequest) {
        try {
            BorrowRecord record = borrowRecordService.returnBook(returnBookRequest.getBorrowRecordId());
            return ResponseEntity.ok(BorrowRecordDTO.toDTO(record));
        } catch (Exception e) {
            log.error("Error returning book: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('PATRON')")
    @PostMapping("/self-return")
    public ResponseEntity<?> selfReturnBook(@RequestBody ReturnBookRequestDTO returnBookRequest) {
        try {
            Long userId = JwtUtils.getUserId();
            BorrowRecord record = borrowRecordService.returnBook(returnBookRequest.getBorrowRecordId());
            if(record.getUser().getId().longValue() != userId.longValue()){
                log.error(NOT_BORROWED_BY_USER.getMessage(), returnBookRequest.getBorrowRecordId());
                throw new RuntimeException(NOT_BORROWED_BY_USER.getMessage());
            }
            return ResponseEntity.ok(BorrowRecordDTO.toDTO(record));
        } catch (Exception e) {
            log.error("Error returning book: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // Get borrowing history for a user
    @PreAuthorize("hasRole('LIBRARIAN')")
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<BorrowRecordDTO>> getBorrowHistoryByUser(@PathVariable Long userId) {
        List<BorrowRecord> history = borrowRecordService.getBorrowHistoryByUser(userId);
        List<BorrowRecordDTO> historyDTO = new ArrayList<>();
        history.forEach(borrowRecord -> {
            historyDTO.add(BorrowRecordDTO.toDTO(borrowRecord));
        });
        return ResponseEntity.ok(historyDTO);
    }

    // Get user's own borrow records for librarians and patrons
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('PATRON')")
    @GetMapping("/my-borrows")
    public ResponseEntity<List<BorrowRecordDTO>> getMyBorrowRecords() {
        Long userId = JwtUtils.getUserId();
        List<BorrowRecord> allRecords = borrowRecordService.getAllBorrowRecords(userId);
        List<BorrowRecordDTO> allDTO = new ArrayList<>();
        allRecords.forEach(borrowRecord -> {
            allDTO.add(BorrowRecordDTO.toDTO(borrowRecord));
        });
        return ResponseEntity.ok(allDTO);
    }


    // Get all borrow records (for librarians)
    @PreAuthorize("hasRole('LIBRARIAN')")
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
    @PreAuthorize("hasRole('LIBRARIAN')")
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