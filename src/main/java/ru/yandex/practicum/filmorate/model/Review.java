package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Builder
@Valid
public class Review {
    private int reviewId;
    @NotNull
    private String content;
    @NotNull
    @Getter
    public Boolean isPositive;
    @NotNull
    private Integer userId;
    @NotNull
    private Integer filmId;
    private int useful;
}

