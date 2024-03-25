package ru.yandex.practicum.filmorate.service.mparating;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mparating.MpaRatingStorage;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для {@link MpaRating}.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MpaRatingServiceImpl implements MpaRatingService {
    private final MpaRatingStorage mpaRatingStorage;

    @Override
    public List<MpaRating> get() {
        return mpaRatingStorage.get();
    }

    @Override
    public MpaRating getById(Integer id) {
        Optional<MpaRating> optionalMpaRating = mpaRatingStorage.getById(id);

        if (optionalMpaRating.isEmpty()) {
            throw new ValidationException(HttpStatus.NOT_FOUND, "MPA рейтинг с id = " + id + " не найден");
        }

        return optionalMpaRating.get();
    }
}
