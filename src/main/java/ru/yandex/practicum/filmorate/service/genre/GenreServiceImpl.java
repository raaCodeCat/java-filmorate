package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для {@link Genre}.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreDbStorage genreDbStorage;

    @Override
    public List<Genre> get() {
        return genreDbStorage.get();
    }

    @Override
    public Genre getById(Integer id) {
        Optional<Genre> genreOptional = genreDbStorage.getById(id);

        if (genreOptional.isEmpty()) {
            throw new ValidationException(HttpStatus.NOT_FOUND, "Жанр с id = " + id + " не найден");
        }

        return genreOptional.get();
    }
}
