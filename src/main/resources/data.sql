-- Delete table section(reuse) -------------------------------------------------

 DELETE FROM public.film_user_like;
 DELETE FROM public.film_genre;
 DELETE FROM public.user_friend;
--
 DELETE FROM public.FILM;
 ALTER TABLE public.FILM ALTER COLUMN film_id RESTART WITH 1;
--
 DELETE FROM public.GENRE ;
 ALTER TABLE public.GENRE ALTER COLUMN genre_id RESTART WITH 1;
--
 DELETE FROM public.users;
 ALTER TABLE public.users ALTER COLUMN user_id RESTART WITH 1;
--
 DELETE FROM public.mpa;
 ALTER TABLE public.mpa ALTER COLUMN mpa_id RESTART WITH 1;


--
DELETE FROM public.film_director;
DELETE FROM public.director ;
ALTER TABLE public.director ALTER COLUMN director_id RESTART WITH 1;



MERGE INTO mpa (mpa_id, name)
    VALUES  (1,'G'),
            (2,'PG'),
            (3,'PG-13'),
            (4,'R'),
            (5,'NC-17');

MERGE INTO genre (genre_id, name)
    VALUES  (1,'Комедия'),
            (2,'Драма'),
            (3,'Мультфильм'),
            (4,'Триллер'),
            (5,'Документальный'),
            (6,'Боевик');