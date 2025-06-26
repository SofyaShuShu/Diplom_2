package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserCreateWithoutRequiredFieldsTest {
    private User user;

    @Before
    @Step("Setup base URL")
    public void setUp() {
        Utils.setUp();
    }

    @Test
    @DisplayName("Create user without email")
    public void createUserWithoutEmailTest(){
        user = UserGenerator.generateUserWithoutEmail();

        Response response = UserUtils.userCreate(user);
        response.then().statusCode(SC_FORBIDDEN);
        response.then().body("message", equalTo(ErrorsMessages.REQUIRED_FIELDS_MESSAGE));
    }

    @Test
    @DisplayName("Create user without password")
    public void createUserWithoutPasswordTest(){
        user = UserGenerator.generateUserWithoutPassword();

        Response response = UserUtils.userCreate(user);
        response.then().statusCode(SC_FORBIDDEN);
        response.then().body("message", equalTo(ErrorsMessages.REQUIRED_FIELDS_MESSAGE));
    }

    @Test
    @DisplayName("Create user without name")
    public void createUserWithoutNameTest(){
        user = UserGenerator.generateUserWithoutName();

        Response response = UserUtils.userCreate(user);
        response.then().statusCode(SC_FORBIDDEN);
        response.then().body("message", equalTo(ErrorsMessages.REQUIRED_FIELDS_MESSAGE));
    }

    @After
    @Step("Deleting the created user from the database")
    public void tearDown(){
        String accessToken = UserUtils.getAccessToken(user);
        UserUtils.userDelete(accessToken);
    }
}

