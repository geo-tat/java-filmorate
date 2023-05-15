package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;


import javax.validation.constraints.*;
import java.time.LocalDate;

@Data

public class Film {
    private int id;
    @NonNull
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;

    private LocalDate releaseDate;
    @NotNull
    @Min(0)
    private long duration;

    public Film(@NonNull String name, String description, LocalDate releaseDate, long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
