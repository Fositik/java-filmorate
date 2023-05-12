package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@Service
//@NoArgsConstructor
@AllArgsConstructor
public class RatingMPA {
    @Positive (message = "Идентификатор рейтинга не может быть отрицатнельным значением")
    private Integer id;
    @NotBlank (message = "Название рейтинга не может быть пустым")
    private String name;
}
