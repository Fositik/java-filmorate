package ru.yandex.practicum.filmorate.util.idfactory;

import ru.yandex.practicum.filmorate.model.User;

import java.util.TreeSet;

public class UserIdFactory {
    private static TreeSet<Long> usedIds = new TreeSet<>();

    public static User setUniqueUserId(User user) {
        if (usedIds.isEmpty()) {
            usedIds.add(1L);
            user.setId(1L);
        } else {
            final Long lastId = usedIds.last() + 1;
            usedIds.add(lastId);
            user.setId(lastId);
        }
        return user;
    }
}