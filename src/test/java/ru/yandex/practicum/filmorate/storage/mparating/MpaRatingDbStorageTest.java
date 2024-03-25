package ru.yandex.practicum.filmorate.storage.mparating;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.MpaRating;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaRatingDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    private MpaRatingDbStorage mpaRatingDbStorage;

    @BeforeEach
    void setUp() {
        mpaRatingDbStorage = new MpaRatingDbStorage(jdbcTemplate);
    }

    @Test
    void get() {
        List<MpaRating> ratings = mpaRatingDbStorage.get();

        assertNotNull(ratings);
        assertEquals(5, ratings.size());
    }

    @Test
    void getById() {
        Optional<MpaRating> rating = mpaRatingDbStorage.getById(1);

        assertTrue(rating.isPresent());
        assertEquals(1, rating.get().getId());
        assertEquals("G", rating.get().getName());
        assertEquals("Нет возрастных ограничений", rating.get().getDescription());
    }
}