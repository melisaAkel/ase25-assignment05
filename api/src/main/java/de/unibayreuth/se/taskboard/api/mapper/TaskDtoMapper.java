package de.unibayreuth.se.taskboard.api.mapper;

import de.unibayreuth.se.taskboard.api.dtos.TaskDto;
import de.unibayreuth.se.taskboard.api.dtos.UserDto;
import de.unibayreuth.se.taskboard.business.domain.Task;
import de.unibayreuth.se.taskboard.business.domain.User;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import de.unibayreuth.se.taskboard.business.ports.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Mapper(componentModel = "spring")
@ConditionalOnMissingBean // prevent IntelliJ warning about duplicate beans
@NoArgsConstructor
public abstract class TaskDtoMapper {
//DODO: Fix this mapper after resolving the other DODOs.
//I changed from private to protected it give error in one of my run, thne i changed.
    @Autowired
    protected UserService userService;
    @Autowired
    protected UserDtoMapper userDtoMapper;

    protected boolean utcNowUpdated = false;
    protected LocalDateTime utcNow;

    @Mapping(target = "assignee", expression = "java(getUserById(source.getAssigneeId()))")
    public abstract TaskDto fromBusiness(Task source);


    @Mapping(target = "status", source = "status", defaultValue = "TODO")
    @Mapping(target = "assigneeId", expression = "java(source.getAssignee() == null ? null : source.getAssignee().id())")
    @Mapping(target = "createdAt", expression = "java(mapTimestamp(source.getCreatedAt()))")
    @Mapping(target = "updatedAt", expression = "java(mapTimestamp(source.getUpdatedAt()))")
    public abstract Task toBusiness(TaskDto source);

    protected UserDto getUserById(UUID userId) {
    if (userId == null) {
            return null;
        }
        User user = userService.getUserById(userId);
        return user == null ? null : userDtoMapper.fromBusiness(user);
    }

    protected LocalDateTime mapTimestamp (LocalDateTime timestamp) {
        if (timestamp == null) {
            // ensure that createdAt and updatedAt use exactly the same timestamp
            if (!utcNowUpdated) {
                utcNow = LocalDateTime.now(ZoneId.of("UTC"));
                utcNowUpdated = true;
            } else {
                utcNowUpdated = false;
            }
            return utcNow;
        }
        return timestamp;
    }
}

