DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS film CASCADE;
DROP TABLE IF EXISTS user_friend;
DROP TABLE IF EXISTS film_user_like;
DROP TABLE IF EXISTS film_genre;


CREATE TABLE IF NOT EXISTS users
(
    user_id  int PRIMARY KEY AUTO_INCREMENT,
    login    varchar,
    email    varchar,
    name     varchar,
    birthday date
);

CREATE TABLE IF NOT EXISTS mpa
(
    mpa_id int PRIMARY KEY AUTO_INCREMENT,
    name varchar UNIQUE
);

CREATE TABLE IF NOT EXISTS film
(
    film_id      int PRIMARY KEY AUTO_INCREMENT,
    name         varchar NOT NULL,
    description varchar(200),
    release_date date,
    duration     long CHECK(duration > 0),
    mpa_id         int,
    FOREIGN KEY (mpa_id) REFERENCES mpa (mpa_id),
    CONSTRAINT release_date_check CHECK (release_date > '1895-12-28')
    );

CREATE TABLE IF NOT EXISTS genre
(
    genre_id int UNIQUE PRIMARY KEY AUTO_INCREMENT,
    name     varchar UNIQUE
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  int,
    genre_id int,
    CONSTRAINT genre_fk
    FOREIGN KEY (film_id) REFERENCES film(film_id),
    CONSTRAINT genre_fk_two
    FOREIGN KEY (genre_id) REFERENCES genre(genre_id),
    UNIQUE (film_id, genre_id)
    );

CREATE TABLE IF NOT EXISTS user_friend
(
    user_id      int,
    friend_id    int,
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (friend_id) REFERENCES users (user_id),
    UNIQUE (user_id, friend_id)
    );

CREATE TABLE IF NOT EXISTS film_user_like
(
    film_id int,
    user_id int,
    FOREIGN KEY (film_id) REFERENCES film (film_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    UNIQUE (film_id, user_id)
    );