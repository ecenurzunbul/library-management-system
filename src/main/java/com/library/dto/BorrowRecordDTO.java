package com.library.dto;

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
}
