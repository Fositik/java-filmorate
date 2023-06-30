package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FriendService;

import java.util.List;

@RestController
@RequestMapping("/users/{id}/friends")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendController {
    private final FriendService friendService;

    @PutMapping("/{friendId}")
    public void addFriend(@PathVariable(name = "id") Long id,
                          @PathVariable(name = "friendId") Long friendId) {
        log.info("Добавление пользователем с id={} в друзья: пользователя с id={}", id, friendId);
        friendService.addFriend(id, friendId);
    }

    @DeleteMapping("{friendId}")
    public void removeFriend(@PathVariable(name = "id") long id,
                             @PathVariable(name = "friendId") long friendId) {
        log.info("Удаление пользователем с id={} из друзей: пользователя с id={}", id, friendId);
        friendService.removeFriend(id, friendId);
    }

    @GetMapping("/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable(name = "id") long id,
                                       @PathVariable(name = "otherId") long otherId) {
        return friendService.getCommonFriends(id, otherId);
    }

    @GetMapping
    public List<User> getFriends(@PathVariable(name = "id") long id) {
        return friendService.getFriends(id);
    }
}
