package ru.alexgur.blog.post.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ru.alexgur.blog.post.interfaces.PostImageService;
import ru.alexgur.blog.post.model.Image;
import ru.alexgur.blog.system.exception.NotFoundException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
@Validated
public class PostImageController {
    private final PostImageService postImageService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void load(@PathVariable(name = "id") Long id, HttpServletResponse response) {
        Optional<Image> imgOpt = postImageService.load(id);
        if (imgOpt.isPresent()) {
            returnImageInStream(response, imgOpt.get());
        }
        throw new NotFoundException("Файл не найден");
    }

    private void returnImageInStream(HttpServletResponse response, Image img) {
        try {
            InputStream inputStream = img.getInputStream();

            response.setContentType(img.getContentType());
            response.setHeader("Content-Length", img.getContentLength().toString());

            OutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }
}

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.multipart.MultipartFile;

// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;

// @Controller
// public class ImageUploadController {

// @Value("${upload.dir.images}")
// private String uploadDir;

// @PostMapping("/upload")
// public String uploadFile(@RequestParam("file") MultipartFile file) {
// try {
// byte[] bytes = file.getBytes();
// Path path = Paths.get(uploadDir + file.getOriginalFilename());
// Files.write(path, bytes);
// return "redirect:/success";
// } catch (IOException e) {
// e.printStackTrace();
// return "redirect:/error";
// }
// }
