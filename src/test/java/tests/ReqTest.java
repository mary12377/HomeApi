package tests;



import io.restassured.response.Response;
import models.User;
import models.UserData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;



public class ReqTest extends TestBase {
    @Test
    @DisplayName("Checking email using Groovy")
    void checkEmailUsingGroovy() {
        given()
                .spec(request)
                .when()
                .get("/users")
                .then()
                .spec(responseSpec200)
                .log().body()
                .body("data.findAll{it.email =~/.*?@reqres.in/}.email.flatten()",
                        hasItems("eve.holt@reqres.in"));
    }

    @Test
    @DisplayName("Successful registration")
    void successfulRegister() {

        User user = new User();
        user.setEmail("eve.holt@reqres.in");
        user.setPassword("pistol");

        User response = given()
                .spec(request)
                .body(user)
                .when()
                .post("/register")
                .then()
                .spec(responseSpec200)
                .log().body()
                .extract().as(User.class);

        assertEquals("4", response.getId());
        assertEquals("QpwL5tke4Pnpja7X4", response.getToken());
    }

    @Test
    @DisplayName("Successful authorization")
    void successfulLogin() {
        User user = new User();
        user.setEmail("eve.holt@reqres.in");
        user.setPassword("cityslicka");

        User response = given()
                .spec(request)
                .body(user)
                .when()
                .post("/login")
                .then()
                .spec(responseSpec200)
                .log().body()
                .extract().as(User.class);

        assertEquals(response.getToken(), "QpwL5tke4Pnpja7X4");
    }

    @Test
    @DisplayName("Unsuccessful authorization")
    void unsuccessfulLogin() {
        User user = new User();
        user.setEmail("peter@klaven");

        User response = given()
                .spec(request)
                .body(user)
                .when()
                .post("/login")
                .then()
                .statusCode(400)
                .log().body()
                .extract().as(User.class);

        assertEquals(response.getError(), "Missing password");
    }

    @Test
    @DisplayName("Creation of a new user")
    void createUser() {
        User user = new User();
        user.setName("morpheus");
        user.setJob("leader");

        User response = given()
                .spec(request)
                .body(user)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .log().body()
                .extract().as(User.class);
        assertEquals(response.getName(), user.getName());
        assertEquals(response.getJob(), user.getJob());
    }

    @Test
    @DisplayName("Updating a user")
    void updateUser() {
        User user = new User();
        user.setName("morpheus");
        user.setJob("zion resident");

        User response = given()
                .spec(request)
                .body(user)
                .when()
                .put("/users/2")
                .then()
                .spec(responseSpec200)
                .log().body()
                .extract().as(User.class);
        assertEquals(response.getName(), user.getName());
        assertEquals(response.getJob(), user.getJob());
    }

    @Test
    @DisplayName("Search for a user")
    void singleUser() {

        UserData response = given()
                .spec(request)
                .when()
                .get("/users/2")
                .then()
                .spec(responseSpec200)
                .log().body()
                .extract().as(UserData.class);

        assertEquals("2", response.getUser().getId());
        assertEquals("janet.weaver@reqres.in", response.getUser().getEmail());
    }

    @Test
    @DisplayName("Search for a non-existent user")
    void singleUserNotFound() {

        Response response = given()
                .spec(request)
                .when()
                .get("/users/23")
                .then()
                .statusCode(404)
                .log().body()
                .extract().response();
        assertThat(response).isNotNull();
    }
}
