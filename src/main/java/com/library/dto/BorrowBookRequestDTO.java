package com.library.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BorrowBookRequestDTO {
    Long bookId;
    Long userId;
}
