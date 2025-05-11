package com.library.constants;

import lombok.Getter;

@Getter
public enum ErrorCode {
    BOOK_NOT_FOUND("BOOK_NOT_FOUND", "Book not found"),
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found"),
    BORROW_RECORD_NOT_FOUND("BORROW_RECORD_NOT_FOUND", "Borrow record not found"),
    BOOK_ALREADY_BORROWED("BOOK_ALREADY_BORROWED", "Book is already borrowed"),
    BOOK_ALREADY_RETURNED("BOOK_ALREADY_RETURNED", "Book is already returned"),
    BOOK_NOT_BORROWED("BOOK_NOT_BORROWED", "Book is not borrowed"),
    INVALID_RETURN_DATE("INVALID_RETURN_DATE", "Invalid return date"),
    INVALID_BORROW_DATE("INVALID_BORROW_DATE", "Invalid borrow date"),
    USER_MAXIMUM_BOOKS_BORROWED("USER_MAXIMUM_BOOKS_BORROWED", "User has reached the maximum number of borrowed books");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
