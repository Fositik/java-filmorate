CREATE TABLE IF NOT EXIST users
(
    user_id   INTEGER AUTO_INCREMENT PRIMARY KEY,
    login     VARCHAR,
    user_name VARCHAR,
    birthday  DATE,
    email     VARCHAR
);

CREATE TABLE IF NOT EXIST user_friends
(
    friendship_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    user_id       INTEGER REFERENCES users (user_id),
    friend_id     INTEGER REFERENCES users (user_id),
    status_id     INTEGER REFERENCES friendship_status (status_id) DEFAULT 2
);

CREATE TABLE IF NOT EXIST friendship_status
(
    status_id   INTEGER AUTO_INCREMENT PRIMARY KEY,
    status_name VARCHAR
);

CREATE TABLE IF NOT EXIST films
(
    film_id     INTEGER AUTO_INCREMENT PRIMARY KEY,
    film_name   VARCHAR,
    description VARCHAR(200),
    relise_date DATE,
    duration    INTEGER,
    reating_id  INTEGER REFERENCES ratings (rating_id)
);

CREATE TABLE IF NOT EXIST film_user_likes
(
    like_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    film_id INTEGER REFERENCES films (film_id),
    user_id INTEGER REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXIST film_genres
(
    like_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    film_id INTEGER REFERENCES films (film_id),
    genre_id INTEGER REFERENCES genres (genre_id)
);

CREATE TABLE IF NOT EXIST genres
(
    genre_id   INTEGER AUTO_INCREMENT PRIMARY KEY,
    genre_name VARCHAR
);

CREATE TABLE IF NOT EXIST ratings
(
    rating_id   INTEGER AUTO_INCREMENT PRIMARY KEY,
    rating_name VARCHAR
);

