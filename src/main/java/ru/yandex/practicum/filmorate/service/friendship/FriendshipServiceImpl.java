package ru.yandex.practicum.filmorate.service.friendship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для {@link Friendship}.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FriendshipServiceImpl implements FriendshipService {
    @Qualifier("friendshipDbStorage")
    private final FriendshipStorage friendshipStorage;

    @Override
    public List<Friendship> get() {
        return friendshipStorage.get();
    }

    @Override
    public Friendship getById(Integer id) {
        Optional<Friendship> friendshipOptional = friendshipStorage.getById(id);

        if (friendshipOptional.isEmpty()) {
            throw new ValidationException(HttpStatus.NOT_FOUND, "Запись о дружбе с id = " + id + " не найдена");
        }

        return friendshipOptional.get();
    }

    @Override
    public Friendship create(Friendship friendship) {
        return friendshipStorage.create(friendship);
    }

    @Override
    public Friendship update(Friendship friendship) {
        return friendshipStorage.update(friendship.getId(), friendship);
    }
}
