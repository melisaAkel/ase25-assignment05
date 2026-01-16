package de.unibayreuth.se.taskboard.api.controller;

import de.unibayreuth.se.taskboard.api.dtos.UserDto;
import de.unibayreuth.se.taskboard.business.domain.User;
import de.unibayreuth.se.taskboard.business.ports.UserService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Convert;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@OpenAPIDefinition(
        info = @Info(title = "TaskBoard", version = "0.0.1")
)
@Tag(name = "Users")
@Controller
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @ResponseBody
    public List<UserDto> getAllUsers() {
        //first fetching all users then mapping to dto
        return userService.getAllUsers().stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public UserDto getUserById(@PathVariable UUID id) {
        //Fetch user by ID then to the DTO
        return toDto(userService.getUserById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserDto createUser(@RequestBody UserDto userDto) {
        //returns 201 as created
        // This time from dto to dom object. Service creates then converts to dto object
        User created = userService.createUser(toDomain(userDto));
        return toDto(created);
    }
    //the next two function does what name suggests.
    private UserDto toDto(User user) {
    return new UserDto(
            user.getId(),
            user.getCreatedAt(),
            user.getName()
    );
}


    private User toDomain(UserDto dto) {
    User u = new User();
    u.setId(dto.id());
    u.setName(dto.name());
    if (dto.createdAt() != null) {
        u.setCreatedAt(dto.createdAt());
    }
    return u;
}
}
