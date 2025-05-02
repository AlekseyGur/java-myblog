package ru.alexgur.blog.configuration;

import ru.alexgur.blog.system.config.DataSourceConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@Profile("test")
@ComponentScan(basePackages = {
        "ru.alexgur.blog" }, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = DataSourceConfig.class))
public class TestWebConfiguration {
}
