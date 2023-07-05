package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
public class ReviewController {
    ReviewService service;

    @Autowired
    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @PostMapping("/reviews")
    public Review addReview(@RequestBody @Valid Review review) {
        return service.addReview(review);
    }

    @PutMapping("/reviews")
    public Review updateReview(@RequestBody @Valid Review review) {
        return service.updateReview(review);
    }

    @DeleteMapping("/reviews/{id}")
    public boolean deleteReview(@PathVariable int id) {
        return service.deleteReview(id);
    }

    @GetMapping("/reviews/{id}")
    public Review getReviewById(@PathVariable int id) {
        return service.getReviewById(id);
    }

    @GetMapping("/reviews")
    public Collection<Review> getReviewsByFilm(@RequestParam(defaultValue = "10") int count,
                                               @RequestParam(required = false) Integer filmId) {
        return service.getReviewsByFilm(count, filmId);
    }

    @PutMapping("/reviews/{id}/like/{userId}")
    public void addLikeToReview(@PathVariable int id, @PathVariable int userId) {
        service.addLikeToReview(id, userId);
    }

    @PutMapping("/reviews/{id}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable int id, @PathVariable int userId) {
        service.addDislikeToReview(id, userId);
    }

    @DeleteMapping("/reviews/{id}/like/{userId}")
    public void deleteLikeToReview(@PathVariable int id, @PathVariable int userId) {
        service.deleteLikeToReview(id, userId);
    }

    @DeleteMapping("/reviews/{id}/dislike/{userId}")
    public void deleteDislikeToReview(@PathVariable int id, @PathVariable int userId) {
        service.deleteDislikeToReview(id, userId);
    }
}
