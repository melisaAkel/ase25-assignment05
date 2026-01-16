package de.unibayreuth.se.taskboard.api.dtos;

//DODO: Add DTO for users.
import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
        UUID id,
        LocalDateTime createdAt,
        String name
) { }
