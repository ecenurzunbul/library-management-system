package com.library.dto;

import com.library.model.BorrowRecord;
import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BorrowRecordDTO {

    private Long id;
    
    private Long bookId;
    
    private Long userId;
    
    private LocalDate borrowDate;
    
    private LocalDate dueDate;
    
    private LocalDate returnDate;

    private boolean returned;

    public static BorrowRecordDTO toDTO(BorrowRecord record){
        BorrowRecordDTO borrowRecordDTO = new BorrowRecordDTO();
        borrowRecordDTO.setId(record.getId());
        borrowRecordDTO.setBookId(record.getBook().getId());
        borrowRecordDTO.setUserId(record.getUser().getId());
        borrowRecordDTO.setBorrowDate(record.getBorrowDate());
        borrowRecordDTO.setDueDate(record.getDueDate());
        borrowRecordDTO.setReturnDate(record.getReturnDate());
        borrowRecordDTO.setReturned(record.isReturned());
        return borrowRecordDTO;
    }

}
