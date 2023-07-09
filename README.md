# java-filmorate. Спринт 12: Групповой проект

---

## Состав команды

- [Георгий Татевосян](https://github.com/geo-tat) - тим лид
- [Дарья Серова](https://github.com/dserova)
- [Руслан Захаров](https://github.com/14winter)
- [Ольга Образцова](https://github.com/olgaobraztsova)

## Распределение задач ([Доска](https://github.com/users/geo-tat/projects/1))

- Георгий:
- [x] Функциональность "Удаление фильмов и пользователей"
- [x] Функциональность "Отзывы"
- Ольга:
- [x] Функциональность "Рекомендации"
- [x] Функциональность "Поиск"
- Дарья:
- [x] Функциональность "Популярные фильмы"
- [x] Функциональность "Фильмы по режиссерам"
- Руслан
- [x] Функциональность "Лента событий"
- [x] Функциональность "Общие фильмы"

## Реализация
- Для реализации функциональности по режиссерам добавлен класс Director, и соответствующие контроллер, сервис и хранилище;
- Для реализации функциональности по Ленте событий добавлен класс Feed и соответствующее хранилище
- Для реализации функциональности по отзывам добавлен класс Review, соответствующие контроллер сервис, хранилище;
- Функциональность по рекомендациям добавлена в существующий класс LikeDbStorage, эндпоинт обрабатывается в FilmController;
- Функциональность по поиску добавлена в существующий класс FilmDbStorage, эндпоинт обрабатывается в FilmController;
- Функциональность по удалению фильмов и пользователей реализована в существующих классах FilmDbStorage и UserDbStorage;
- Функциональность по общим фильмам реализована в существующем классе FilmDbStorage, и эндпоинт обрабатывается в FilmController;
- Функциональность по популярным фильмам реализована в существующем классе LikeDbStorage, и эндпоинт обрабатывается в FilmController;


## Структура базы данных

![Filmorate Database Diagram](ER.png)
