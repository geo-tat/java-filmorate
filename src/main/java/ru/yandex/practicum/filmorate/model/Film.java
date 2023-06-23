package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;


import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Valid
@Builder
@EqualsAndHashCode(of = "id")
public class Film {
    private int id;
    @NonNull
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private LocalDate releaseDate;
    private long duration;
    private List<Genre> genres;
    private MPA mpa;
}
