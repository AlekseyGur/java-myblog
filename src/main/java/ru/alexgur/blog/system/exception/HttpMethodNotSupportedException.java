package ru.alexgur.blog.system.exception;

public class HttpMethodNotSupportedException extends RuntimeException {
    public HttpMethodNotSupportedException(String message) {
        super(message);
    }
}