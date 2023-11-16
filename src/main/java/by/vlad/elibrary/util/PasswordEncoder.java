package by.vlad.elibrary.util;

public interface PasswordEncoder {

    String encode(String rawPassword);

    boolean verifyPassword(String rawPassword, String encodedPassword);
}
