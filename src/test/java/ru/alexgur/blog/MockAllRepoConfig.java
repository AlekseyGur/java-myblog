package ru.alexgur.blog;

import javax.sql.DataSource;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = Main.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application.yaml")
@TestConfiguration
public class MockAllRepoConfig {

    // @Bean
    // public ExternalApiClient mockExternalApiClient() {
    // return Mockito.mock(ExternalApiClient.class); // Создание мок-объекта
    // }
}