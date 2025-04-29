package ru.alexgur.blog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import ru.alexgur.blog.comment.CommentServiceImpl;
import ru.alexgur.blog.comment.interfaces.CommentService;
import ru.alexgur.blog.comment.interfaces.CommentStorage;
import ru.alexgur.blog.comment.mapper.CommentRowMapper;
import ru.alexgur.blog.comment.repository.CommentSqlRepository;
import ru.alexgur.blog.post.PostServiceImpl;
import ru.alexgur.blog.post.interfaces.PostService;
import ru.alexgur.blog.post.interfaces.PostStorage;
import ru.alexgur.blog.post.mapper.PostRowMapper;
import ru.alexgur.blog.post.repository.PostSqlRepository;
import ru.alexgur.blog.tag.TagServiceImpl;
import ru.alexgur.blog.tag.interfaces.TagService;
import ru.alexgur.blog.tag.interfaces.TagStorage;
import ru.alexgur.blog.tag.mapper.TagRowMapper;
import ru.alexgur.blog.tag.repository.TagSqlRepository;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "ru.alexgur")
public class WebConfigurationTest {
    @Autowired
    private NamedParameterJdbcTemplate njdbcTemplate;

    @Autowired
    private TagRowMapper tagRowMapper;

    @Autowired
    private PostRowMapper postRowMapper;

    @Autowired
    private CommentRowMapper commentRowMapper;

    // ------------------

    @Bean
    public TagService tagService() {
        return new TagServiceImpl(tagStorage());
    }

    @Bean
    public TagStorage tagStorage() {
        return new TagSqlRepository(njdbcTemplate, tagRowMapper);
    }

    // ------------------

    @Bean
    public PostService postService() {
        return new PostServiceImpl(postStorage(), tagService(), commentService());
    }

    @Bean
    public PostStorage postStorage() {
        return new PostSqlRepository(njdbcTemplate, postRowMapper);
    }

    // ------------------

    @Bean
    public CommentService commentService() {
        return new CommentServiceImpl(commentStorage(), postService());
    }

    @Bean
    public CommentStorage commentStorage() {
        return new CommentSqlRepository(njdbcTemplate, commentRowMapper);
    }
}
