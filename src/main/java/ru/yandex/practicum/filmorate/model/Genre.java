package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Genre {

    private int id;

    private String name;

    public  Genre (int id){
        this.id = id;
    }
}
