package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserCreateWithoutRequiredFields {
    private User user;

    @Before
    @Step("Setup base URL")
    public void setUp() {
        Utils.setUp();
    }

    @Test
    @Step("Create user without email")
    public void createUserWithoutEmail(){
        user = new User(null, "frodotest", "Frodo");

        Response response = UserUtils.userCreate(user);
        response.then().statusCode(SC_FORBIDDEN);
        String expectedMessage = "Email, password and name are required fields";
        response.then().body("message", equalTo(expectedMessage));
    }

    @Test
    @Step("Create user without password")
    public void createUserWithoutPassword(){
        user = new User("frodo@frodo.com", null, "Frodo");

        Response response = UserUtils.userCreate(user);
        response.then().statusCode(SC_FORBIDDEN);
        String expectedMessage = "Email, password and name are required fields";
        response.then().body("message", equalTo(expectedMessage));
    }

    @Test
    @Step("Create user without name")
    public void createUserWithoutName(){
        user = new User("frodo@frodo.com", "frodotest", null);

        Response response = UserUtils.userCreate(user);
        response.then().statusCode(SC_FORBIDDEN);
        String expectedMessage = "Email, password and name are required fields";
        response.then().body("message", equalTo(expectedMessage));
    }

    @After
    @Step("Deleting the created user from the database")
    public void tearDown(){
        String accessToken = UserUtils.getAccessToken(user);
        UserUtils.userDelete(accessToken);
    }
}

