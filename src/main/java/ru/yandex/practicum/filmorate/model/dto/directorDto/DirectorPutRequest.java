package ru.yandex.practicum.filmorate.model.dto.directorDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DirectorPutRequest {

  @NotNull(message = "ID режиссера обязателен")
  private Long id;

  @NotBlank(message = "Имя режиссера обязательно")
  @Size(max = 200)
  private String name;
}
