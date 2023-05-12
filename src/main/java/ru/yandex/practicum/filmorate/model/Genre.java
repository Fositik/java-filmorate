package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Service
@Data
//@NoArgsConstructor
@AllArgsConstructor
public class Genre {
    @Positive (message = "Идентификатор жанра не может быть отрицатнельным значением")
    private Integer id;
    @NotBlank (message = "Название жанра не может быть пустым")
    private String name;
}
