DROP TABLE IF EXISTS film_user_like CASCADE;
DROP TABLE IF EXISTS mpa CASCADE;
DROP TABLE IF EXISTS user_friend CASCADE;
DROP TABLE IF EXISTS film CASCADE;
DROP TABLE IF EXISTS genre CASCADE;
DROP TABLE IF EXISTS film_genre CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS director CASCADE;
DROP TABLE IF EXISTS film_director CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS review_like_dislike CASCADE;

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
    genre_id int PRIMARY KEY AUTO_INCREMENT,
    name     varchar UNIQUE
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  int,
    genre_id int,
    CONSTRAINT pk_film_genre PRIMARY KEY (film_id, genre_id),
    CONSTRAINT genre_fk
    FOREIGN KEY (film_id) REFERENCES film(film_id) ON DELETE CASCADE,
    CONSTRAINT genre_fk_two
    FOREIGN KEY (genre_id) REFERENCES genre(genre_id)
    );

CREATE TABLE IF NOT EXISTS user_friend
(
    user_id      int,
    friend_id    int,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users (user_id) ON DELETE CASCADE,
    UNIQUE (user_id, friend_id)
    );

CREATE TABLE IF NOT EXISTS film_user_like
(
    film_id int,
    user_id int,
    CONSTRAINT pk_ful PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES film (film_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
    );


CREATE TABLE IF NOT EXISTS director
(
    director_id int PRIMARY KEY AUTO_INCREMENT,
    name     varchar UNIQUE
);

CREATE TABLE IF NOT EXISTS film_director
(
    film_id  int,
    director_id int,
    CONSTRAINT pk_film_director PRIMARY KEY (film_id, director_id),
    CONSTRAINT director_fk
    FOREIGN KEY (film_id) REFERENCES film(film_id)
    ON DELETE CASCADE ,
    CONSTRAINT director_fk_two
    FOREIGN KEY (director_id) REFERENCES director(director_id)
    ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS reviews
(
    review_id INT PRIMARY KEY AUTO_INCREMENT,
    content   VARCHAR NOT NULL,
    is_positive BOOLEAN NOT NULL,
    user_id   INT REFERENCES users(user_id),
    film_id   INT REFERENCES film(film_id),
    useful    INT DEFAULT 0,
    CONSTRAINT fk_reviews_film FOREIGN KEY (film_id)
            REFERENCES film (film_id)
            ON DELETE CASCADE,
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id)
            REFERENCES users (user_id)
            ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS review_like_dislike
(
    review_id INT,
    user_id   INT,
    is_like   BOOLEAN,
    CONSTRAINT fk_reviews_like FOREIGN KEY (review_id)
            REFERENCES reviews(review_id)
            ON DELETE CASCADE,
        CONSTRAINT fk_users_like FOREIGN KEY (user_id)
            REFERENCES users(user_id)
            ON DELETE CASCADE
    );



