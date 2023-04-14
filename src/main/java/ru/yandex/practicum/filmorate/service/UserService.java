package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final Map<Integer, Set<Integer>> userFriendIdsMap = new HashMap<>();
@Autowired
public UserService(UserStorage userStorage){
    this.userStorage = userStorage;
}

public User createUser(User newUser) throws ValidationException {
    userStorage.createUser(newUser);
    return newUser;
}

public List<User> getAllUsers(){
    List<User> allUsers = userStorage.getAllUsers();
    return allUsers;
}

public User getUserById(Long userId){
    User user = userStorage.getUserById(userId);
    return user;
}

public User updateUser(User updatedUser){
    userStorage.updateUser(updatedUser);
    return updatedUser;
}

public User remove(Long id){
   User removedUser = userStorage.remove(id);
    return removedUser;
}
    public void addFriend(Integer userId, Integer friendId) {
        userFriendIdsMap.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        //Когда метод calculateIfAbsent вызывается с K, которого еще нет в Map, он добавляет к карте пару "K-V".
        // V определяется лямбда-выражением.
        // В этом случае лямбда-выражение создает новый HashSet для Set<friendId>
        // и добавляет идентификатор друга в набор.
        //
        //Если K уже присутствует в карте, calculateIfAbsent не меняя Map возвращает текущее V,
        //связанное с K. В этом случае лямбда-выражение не выполняется, так как не требуется вычислять V, связанное с K.
        userFriendIdsMap.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        userFriendIdsMap.computeIfAbsent(userId, k -> new HashSet<>()).remove(friendId);
        userFriendIdsMap.computeIfAbsent(friendId, k -> new HashSet<>()).remove(userId);
    }

    public Set<Integer> getCommonFriends(Integer userId, Integer otherId) {
        Set<Integer> friendsOfUser = userFriendIdsMap.getOrDefault(userId, new HashSet<>());
        //Получаем набор друзей пользователя
        Set<Integer> friendsOfUserFriend = userFriendIdsMap.getOrDefault(otherId, new HashSet<>());
        //Получаем набор друзей другого пользователя
        Set<Integer> commonFriends = new HashSet<>(friendsOfUser);
        //Создаем новый набор commonFriends, инициализированный набором друзей пользователя
        commonFriends.retainAll(friendsOfUserFriend);
        //Метод retainAll меняет commonFriends так, чтобы в нем остались только общие элементы
        return commonFriends;
    }

    public Set<Integer> getFriends(Integer user) {
        Set<Integer> friendIds = userFriendIdsMap.getOrDefault(user, new HashSet<>());
        return friendIds;
//        return userList.stream()
//                .filter(u -> friendIds.contains(u.getId()))
//                .collect(Collectors.toList());
    }
}