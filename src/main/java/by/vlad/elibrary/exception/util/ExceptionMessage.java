package by.vlad.elibrary.exception.util;

public class ExceptionMessage {

    public static final String ORDER_NOT_FOUND = "Order not found";
    public static final String BOOK_NOT_FOUND = "Book not found";
    public static final String AUTHOR_NOT_FOUND = "Author not found";
    public static final String GENRE_NOT_FOUND = "Genre not found";
    public static final String PUBLISHER_NOT_FOUND = "Publisher not found";
    public static final String CLIENT_NOT_FOUND = "Client not found";
    public static final String MISMATCH_CLIENT_AND_ORDER_OWNER = "Mismatch between client and order owner";
    public static final String WRONG_ORDER_STATUS = "Wrong order status";
    public static final String BOOK_IS_ALREADY_IN_ORDER = "Book is already in this order";
    public static final String ORDER_IS_ALREADY_EMPTY = "Order is already empty";
    public static final String BOOK_IS_NOT_AVAILABLE_IN_CURRENT_ORDER = "The book is not available in the current order";
    public static final String AUTHOR = "Author";
    public static final String PUBLISHER = "Publisher";
    public static final String GENRE = "Genre";
    public static final String _NOT_FOUND = " not found";
    public static final String USER_EMAIL_ALREADY_EXISTS = "User with this email is already exists";
    public static final String PASSWORDS_MISMATCH = "Passwords must be identical";

    private ExceptionMessage(){
    }
}
