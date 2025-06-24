package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserCreateTest {
private User user;

    @Before
    @Step("Setup base URL")
    public void setUp() {
        Utils.setUp();
    }

    @Test
    @Step("Create new user with valid date")
    public void createNewUserWithValidDate(){
        user = new User("frodo@frodo.com", "frodotest", "Frodo");

        Response response = UserUtils.userCreate(user);
        response.then().statusCode(SC_OK);
        response.then().body("success", equalTo(true));
    }

    @Test
    @Step("Create two users with duplicate date")
    public void createTwoUsersWithDuplicateDate(){
        user = new User("frodo@frodo.com", "frodotest", "Frodo");
        Response response = UserUtils.userCreate(user);
        //Создаем пользователя с такими же данными
        User userDuplicate = new User("frodo@frodo.com", "frodotest", "Frodo");

        Response responseDuplicate = UserUtils.userCreate(userDuplicate);
        responseDuplicate.then().statusCode(SC_FORBIDDEN);
        String expectedMessage = "User already exists";
        responseDuplicate.then().body("message", equalTo(expectedMessage));
    }

    @After
    @Step("Deleting the created user from the database")
    public void tearDown(){
        String accessToken = UserUtils.getAccessToken(user);
        UserUtils.userDelete(accessToken);
    }
}

