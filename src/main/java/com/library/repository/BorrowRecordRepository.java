package com.library.repository;

import com.library.model.BorrowRecord;
import com.library.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByUser(User user);
    // Additional queries for overdue records can be added later
}
