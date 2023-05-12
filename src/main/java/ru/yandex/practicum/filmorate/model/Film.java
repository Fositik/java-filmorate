package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Film {
    @NotNull
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    //Аннотация @NotBlank указывает, что поле не может быть пустым
    private String name;

    @Size(max = 200, message = "Описание не может быть длиннее 200 символов")
    // Аннотация @Size указывает максимальную длину поля.
    private String description;

    @NotNull @PastOrPresent(message = "Дата релиза не может быть в будущем")
    // Аннотация @PastOrPresent указывает, что дата должна быть не раньше текущей даты
    private LocalDate releaseDate;

    @NotNull @Min(value = 1, message = "Продолжительность фильма должна быть положительной")
    // Аннотация @Min указывает минимальное значение для числового поля
    private Integer duration;

  //  private Set<Genre> genres = new HashSet<>();
//
//    private RatingMPA mpa;
}