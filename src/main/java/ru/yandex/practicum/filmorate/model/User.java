package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class User {
    @NotNull
    private Long id;

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Некорректный формат электронной почты")
    //Аннотация @Email проверяет, что поле содержит корректный адрес электронной почты
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы")
    //Аннотация @Pattern проверяет, что поле не содержит пробелов.
    private String login;

    private String name;

    @NotNull
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
//    @JsonIgnoreProperties(value = {"friends"}, allowGetters = true)
//    //Эта аннотация будет игнорировать поле «друзья» при сериализации и десериализации объекта,
//    //чтобы избежать бесконечной рекурсии при сериализации объекта.
//    //allowGetters = true, чтобы разрешить десериализацию для установки значения поля.
//    private Set<User> friends;
}