package de.unibayreuth.se.taskboard;

import de.unibayreuth.se.taskboard.business.domain.Task;
import de.unibayreuth.se.taskboard.business.domain.TaskStatus;
import de.unibayreuth.se.taskboard.business.domain.User;
import de.unibayreuth.se.taskboard.business.ports.TaskService;
import de.unibayreuth.se.taskboard.business.ports.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
@Profile("dev")
@Component
@RequiredArgsConstructor
@Slf4j
class LoadInitialData implements InitializingBean {

    private final TaskService taskService;
    private final UserService userService;

    @Override
    public void afterPropertiesSet() {
        log.info("Deleting existing data...");
        taskService.clear();
        userService.clear();

        log.info("Loading initial data...");
        // it creates the same users
        List<User> users = TestFixtures.createUsers(userService);
        // Create first task and assign to Alice
        Task t1 = new Task("Task 1", "Description 1");
        t1.setStatus(TaskStatus.TODO);
        t1.setAssigneeId(users.get(0).getId());
        taskService.create(t1);
        // Create second task and assign to Bob
        Task t2 = new Task("Task 2", "Description 2");
        t2.setStatus(TaskStatus.TODO);
        t2.setAssigneeId(users.get(1).getId());
        taskService.create(t2);

        log.info("Dev data loaded.");
    }
}
