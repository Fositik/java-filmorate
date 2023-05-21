package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
//@Getter
//@Setter
@Data
@Builder(toBuilder = true)
public class User {


    private Long id;


    private String email;

    private String name;


    private String login;


    private LocalDate birthday;


}