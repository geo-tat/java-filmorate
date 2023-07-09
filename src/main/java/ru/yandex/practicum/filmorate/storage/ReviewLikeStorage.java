package ru.yandex.practicum.filmorate.storage;

public interface ReviewLikeStorage {
    void addLikeToReview(int id, int userId, boolean isLike);

    void deleteLikeToReview(int id, int userId, boolean isLike);

}
