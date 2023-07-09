package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GenreDbStorageTest {
    private final GenreDbStorage genre;

    @Test
    public void getGenresTest() {
        int genresCount = 6;
        assertThat(genre.getAllGenres().size()).isEqualTo(genresCount);
    }

    @Test
    public void getGenresByIdTest() {
        Genre result = genre.getGenreById(1);
        assertThat(result.getName()).isEqualTo("Комедия");
    }

}
