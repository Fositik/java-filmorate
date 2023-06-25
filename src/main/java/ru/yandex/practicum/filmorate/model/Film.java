package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Film {

    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    //Аннотация @NotBlank указывает, что поле не может быть пустым
    private String name;

    @NotNull @Size(max = 200, message = "Описание не может быть длиннее 200 символов")
    // Аннотация @Size указывает максимальную длину поля.
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @NotNull @Min(value = 1, message = "Продолжительность фильма должна быть положительной")
    // Аннотация @Min указывает минимальное значение для числового поля
    private Integer duration;

    private LinkedHashSet<Genre> genres;

    @NotNull
    private RatingMPA mpa;
}