package ru.alexgur.blog.post;

// import java.io.IOException;
// import java.io.InputStream;
// import java.io.OutputStream;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HttpServletBean;

import lombok.RequiredArgsConstructor;
import ru.alexgur.blog.post.interfaces.PostImageService;
import ru.alexgur.blog.post.model.Image;
import ru.alexgur.blog.system.exception.NotFoundException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class PostImageController {
    private final PostImageService postImageService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void load(@PathVariable Long id, HttpServletBean response) {
        Optional<Image> imgOpt = postImageService.load(id);
        if (imgOpt.isPresent()) {
            returnImageInStream(response, imgOpt.get());
        }
        throw new NotFoundException("Файл не найден");
    }

    private void returnImageInStream(HttpServletBean response, Image img) {
        // try {
        // InputStream inputStream = img.getInputStream();

        // response.setContentType(img.getContentType());
        // response.setHeader("Content-Length", img.getContentLength().toString());

        // OutputStream outputStream = response.getOutputStream();
        // byte[] buffer = new byte[4096];
        // int bytesRead;

            // while ((bytesRead = inputStream.read(buffer)) != -1) {
            // outputStream.write(buffer, 0, bytesRead);
            // }

            // inputStream.close();
            // outputStream.close();
            // } catch (IOException e) {
            // response.setStatus(HttpServletBean.);
            // e.printStackTrace();
            // }
    }
}
