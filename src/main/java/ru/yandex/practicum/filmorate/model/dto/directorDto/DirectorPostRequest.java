package ru.yandex.practicum.filmorate.model.dto.directorDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DirectorPostRequest {

  @NotBlank(message = "Имя режиссера обязательно")
  @Size(max = 200)
  private String name;
}
