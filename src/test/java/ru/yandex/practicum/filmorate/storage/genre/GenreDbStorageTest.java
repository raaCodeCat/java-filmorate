package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private GenreDbStorage genreDbStorage;

    @BeforeEach
    void setUp() {
        genreDbStorage = new GenreDbStorage(jdbcTemplate);
    }

    @Test
    void get() {
        List<Genre> genres = genreDbStorage.get();

        assertFalse(genres.isEmpty());
        assertEquals(6, genres.size());
    }

    @Test
    void getById() {
        Genre genre = Genre.builder()
                .id(1)
                .name("Комедия")
                .build();

        Optional<Genre> getGenreOpt = genreDbStorage.getById(1);

        assertThat(getGenreOpt)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(genre);
    }
}