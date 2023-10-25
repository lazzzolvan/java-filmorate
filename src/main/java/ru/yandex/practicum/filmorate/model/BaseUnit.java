package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public abstract class BaseUnit {
    private Long id;
}
