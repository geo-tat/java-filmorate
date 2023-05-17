package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;


import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Valid
public class Film {
    private int id;
    @NonNull
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull
    private LocalDate releaseDate;
    private long duration;

    public Film(String name, String description, LocalDate releaseDate, long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
