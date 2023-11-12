package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
    private Set<Long> usersByLike;

    public void addLike(Long id) {
        if (usersByLike == null) {
            usersByLike = new HashSet<>();
        }
        usersByLike.add(id);
    }

    public Set<Long> getUsersByLike() {
        if (usersByLike == null) {
            usersByLike = new HashSet<>();
        }
        return usersByLike;
    }
}
