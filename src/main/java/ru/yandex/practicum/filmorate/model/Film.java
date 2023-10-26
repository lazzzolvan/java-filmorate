package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
public class Film extends BaseUnit {

    @NotBlank
    private String name;
    @Size(max = 200, min = 1)
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @Min(1)
    private int duration;
}
