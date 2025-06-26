package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
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
    @DisplayName("Create new user with valid date")
    public void createNewUserWithValidDateTest(){
        user = UserGenerator.generateUser();

        Response response = UserUtils.userCreate(user);
        response.then().statusCode(SC_OK);
        response.then().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Create two users with duplicate date")
    public void createTwoUsersWithDuplicateDateTest(){
        user = UserGenerator.generateUser();
        Response response = UserUtils.userCreate(user);

        //Создаем пользователя с такими же данными
        User userDuplicate = new User(user.getEmail(), user.getPassword(), user.getName());

        Response responseDuplicate = UserUtils.userCreate(userDuplicate);
        responseDuplicate.then().statusCode(SC_FORBIDDEN);
        responseDuplicate.then().body("message", equalTo(ErrorsMessages.USER_EXISTS_MESSAGE));
    }

    @After
    @Step("Deleting the created user from the database")
    public void tearDown(){
        String accessToken = UserUtils.getAccessToken(user);
        UserUtils.userDelete(accessToken);
    }
}

