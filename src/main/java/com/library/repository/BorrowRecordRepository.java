package com.library.repository;

import com.library.model.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    List<BorrowRecord> findByUserId(Long userId);

    List<BorrowRecord> findByBookIdAndReturnedFalse(Long bookId);

    List<BorrowRecord> findByDueDateBeforeAndReturnedFalse(LocalDate today);
}
