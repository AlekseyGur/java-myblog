package ru.alexgur.blog.post.model;

import java.io.InputStream;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Image {
    String contentType;
    Long contentLength;
    InputStream inputStream;
}