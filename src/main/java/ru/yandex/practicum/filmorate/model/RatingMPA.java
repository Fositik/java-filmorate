package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingMPA {

    private Integer id;

    private String name;

    public RatingMPA(int id) {
        this.id = id;
    }
}
