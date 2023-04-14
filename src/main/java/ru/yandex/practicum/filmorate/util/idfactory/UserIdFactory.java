package ru.yandex.practicum.filmorate.util.idfactory;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.TreeSet;

public class UserIdFactory {

    public static void setUniqueUserId(List<Long> idList, User user) {
        TreeSet<Long> usedIds = new TreeSet<>(idList);
        if (usedIds.isEmpty()) {
            user.setId(1L);
            return;
        }
        final Long lastId = usedIds.last()+1;
        user.setId(lastId);
    }


}