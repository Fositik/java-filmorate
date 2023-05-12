INSERT INTO genres (genre_id, genre_name)
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик')
    ON CONFLICT DO NOTHING;

INSERT INTO ratings (rating_id, rating_name)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17')
    ON CONFLICT DO NOTHING;

INSERT INTO friendship_status (status_id, status_name)
VALUES (1, 'Подтверждённая'),
       (2, 'Неподтверждённая')
    ON CONFLICT DO NOTHING;