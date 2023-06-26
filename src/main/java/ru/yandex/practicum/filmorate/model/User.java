package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.validator.EmailRFC2822;

import javax.validation.constraints.*;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
@Slf4j
public class User {

    private Long id;

    @NotBlank(message = "Электронная почта не может быть пустой")
    @EmailRFC2822(message = "Некорректный формат электронной почты")
    //Аннотация @Email проверяет, что поле содержит корректный адрес электронной почты
    private String email;

    private String name;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы")
    //Аннотация @Pattern проверяет, что поле не содержит пробелов.
    private String login;


    @NotNull
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}