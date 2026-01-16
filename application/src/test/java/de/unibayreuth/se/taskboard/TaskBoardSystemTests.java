package de.unibayreuth.se.taskboard;

import de.unibayreuth.se.taskboard.api.dtos.TaskDto;
import de.unibayreuth.se.taskboard.api.mapper.TaskDtoMapper;
import de.unibayreuth.se.taskboard.business.domain.Task;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.internal.http.HttpResponseException;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import de.unibayreuth.se.taskboard.business.ports.UserService;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.hasSize;


public class TaskBoardSystemTests extends AbstractSystemTest {

    @Autowired
    private TaskDtoMapper taskDtoMapper;
    @Autowired
    private UserService userService;


    @Test
    void getAllCreatedTasks() {
        List<Task> createdTasks = TestFixtures.createTasks(taskService);

        List<Task> retrievedTasks = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/tasks")
                .then()
                .statusCode(200)
                .body(".", hasSize(createdTasks.size()))
                .and()
                .extract().jsonPath().getList("$", TaskDto.class)
                .stream()
                .map(taskDtoMapper::toBusiness)
                .toList();

        assertThat(retrievedTasks)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("createdAt", "updatedAt") // prevent issues due to differing timestamps after conversions
                .containsExactlyInAnyOrderElementsOf(createdTasks);
    }

@Test
void createAndDeleteTask() {
    TaskDto createRequest = taskDtoMapper.fromBusiness(TestFixtures.getTasks().getFirst());

    TaskDto created = given()
            .contentType(ContentType.JSON)
            .body(createRequest)
            .when()
            .post("/api/tasks")
            .then()
            .statusCode(200)
            .extract()
            .as(TaskDto.class);

    UUID id = created.getId();

    //there is
    when()
            .get("/api/tasks/{id}", id)
            .then()
            .statusCode(200);

    // Delete
    when()
            .delete("/api/tasks/{id}", id)
            .then()
            .statusCode(200);

    // After delete it gives BAD_REQUEST
    try {
        when().get("/api/tasks/{id}", id);
        throw new AssertionError("Expected failure after delete, but request succeeded.");
    } catch (Throwable e) {
        assertThat(e).isInstanceOf(io.restassured.internal.http.HttpResponseException.class);
        io.restassured.internal.http.HttpResponseException hre =
                (io.restassured.internal.http.HttpResponseException) e;
        assertThat(hre.getStatusCode()).isEqualTo(400);
    }
}



    @Test
    void getAllUsers() {
        TestFixtures.createUsers(userService);
        given()
            .contentType(ContentType.JSON)
            .when()
            .get("/api/users")
            .then()
            .statusCode(200)
            .body(".", hasSize(3));
        }
        @Test
        void getUserById() {
                var createdUsers = TestFixtures.createUsers(userService);
                var user = createdUsers.getFirst();
                when()
                .get("/api/users/{id}", user.getId())
                .then()
                .statusCode(200);
        }

    //DODO: Add at least one test for each new endpoint in the users controller (the create endpoint can be tested as part of the other endpoints).
}