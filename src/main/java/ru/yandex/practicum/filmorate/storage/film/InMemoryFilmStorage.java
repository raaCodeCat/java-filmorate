package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;

/**
 * Хранилище в памяти для {@link Film}.
 */
@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    private Integer filmIdCounter = 1;

    @Override
    public List<Film> get() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> getById(Integer id) {
        if (films.containsKey(id)) {
            return Optional.of(films.get(id));
        }

        return Optional.empty();
    }

    @Override
    public Film create(Film film) {
        Integer id = filmIdCounter++;
        log.debug("Фильму {} назначен id = {}.", film, id);

        if (film.getLikedUsers() == null) {
            film.setLikedUsers(new HashSet<>());
        }

        film.setId(id);
        films.put(id, film);

        return films.get(id);
    }

    @Override
    public Film update(Integer id, Film film) {
        if (film.getLikedUsers() == null) {
            film.setLikedUsers(new HashSet<>());
        }

        films.replace(id, film);

        return films.get(id);
    }

    @Override
    public Integer addLike(Integer id, Integer userId) {
        Film film = films.get(id);
        Set<Integer> likes = film.getLikedUsers();
        likes.add(userId);

        return likes.size();
    }

    @Override
    public Integer deleteLike(Integer id, Integer userId) {
        Film film = films.get(id);
        Set<Integer> likes = film.getLikedUsers();
        likes.remove(userId);

        return likes.size();
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return films.values().stream()
                .sorted(comparing((Film film) -> film.getLikedUsers().size(),
                        reverseOrder()).thenComparing(Film::getId))
                .limit(count)
                .collect(Collectors.toList());
    }
}
