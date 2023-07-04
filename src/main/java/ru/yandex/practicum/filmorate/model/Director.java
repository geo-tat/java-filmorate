package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Director {
    private int id;
   /* @NonNull
    @NotBlank*/
    private String name;
}
