//package ru.yandex.practicum.filmorate;
//
//import io.restassured.http.ContentType;
//import io.restassured.response.Response;
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.test.annotation.DirtiesContext;
//import ru.yandex.practicum.filmorate.model.User;
//import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
//
//import java.time.LocalDate;
//
//import static io.restassured.RestAssured.given;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@AutoConfigureTestDatabase
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//public class UserDbStorageTest {
//
//    private UserDbStorage userStorage;
//
//    @Test
//    public void createUser_shouldReturnCreatedUser() {
//        // Создаем пользователя
//        User user = User.builder()
//                .id(1L)
//                .name("Nick Name")
//                .login("dolore")
//                .birthday(LocalDate.parse("1946-08-20"))
//                .email("mail@mail.ru")
//                .build();
//
//        // Выполняем запрос на создание пользователя с помощью RestAssured
//        Response response = given()
//                .contentType(ContentType.JSON)
//                .body(user)
//                .when()
//                .post("/users")
//                .then()
//                .extract()
//                .response();
//
//        // Проверяем, что код ответа сервера равен 200
//        assertEquals(response.getStatusCode(), 200);
//
//        // Проверяем, что созданный пользователь имеет ожидаемые значения полей
//        User createdUser = response.getBody().as(User.class);
//        assertEquals(createdUser.getId(), 1L);
//        assertEquals(createdUser.getName(), "Nick Name");
//        assertEquals(createdUser.getLogin(), "dolore");
//        assertEquals(createdUser.getBirthday(), LocalDate.parse("1946-08-20"));
//        assertEquals(createdUser.getEmail(), "mail@mail.ru");
//    }
//
//
//}