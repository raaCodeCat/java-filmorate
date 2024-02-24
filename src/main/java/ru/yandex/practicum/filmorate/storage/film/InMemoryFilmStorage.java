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

    private final Set<Film> filmsByPopularity = new TreeSet<>(comparing((Film film) -> film.getLikedUsers().size(),
            reverseOrder()).thenComparing(Film::getId));

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
        filmsByPopularity.add(film);

        return films.get(id);
    }

    @Override
    public Film update(Integer id, Film film) {
        if (film.getLikedUsers() == null) {
            film.setLikedUsers(new HashSet<>());
        }

        filmsByPopularity.remove(films.get(id));
        films.replace(id, film);
        filmsByPopularity.add(film);

        return films.get(id);
    }

    @Override
    public Integer addLike(Integer id, Integer userId) {
        Film film = films.get(id);
        filmsByPopularity.remove(film);
        Set<Integer> likes = film.getLikedUsers();
        likes.add(userId);
        filmsByPopularity.add(film);

        return likes.size();
    }

    @Override
    public Integer deleteLike(Integer id, Integer userId) {
        Film film = films.get(id);
        filmsByPopularity.remove(film);
        Set<Integer> likes = film.getLikedUsers();
        likes.remove(userId);
        filmsByPopularity.add(film);

        return likes.size();
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return filmsByPopularity.stream().limit(count).collect(Collectors.toList());
    }
}
