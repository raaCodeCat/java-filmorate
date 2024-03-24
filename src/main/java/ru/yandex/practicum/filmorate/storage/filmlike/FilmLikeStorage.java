package ru.yandex.practicum.filmorate.storage.filmlike;

import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Set;

public interface FilmLikeStorage {
    List<FilmLike> get();

    List<User> getLikedUsersByFilmId(Integer id);

    Integer getLikesCountByFilmId(Integer id);

    Set<Integer> getLikedUsersIdByFilmId(Integer id);
}
