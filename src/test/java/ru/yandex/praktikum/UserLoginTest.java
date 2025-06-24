package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserLoginTest {
    private User user;

    @Before
    @Step("Setup base URL and create new user")
    public void setUp() {
        Utils.setUp();
        user = new User("frodo@frodo.com", "frodotest", "Frodo");
        UserUtils.userCreate(user);
    }

    @Test
    @Step("Login user with valid date")
    public void loginUserUserWithValidDate(){
        Response response = UserUtils.userLogin(user);
        response.then().statusCode(SC_OK);
        response.then().body("success", equalTo(true));
    }

    @Test
    @Step("Login impossible without valid email")
    public void loginUserWithoutCorrectEmail(){
        User userWithoutEmail = new User("frodo@frodo.java", user.getPassword(), user.getName());

        Response response = UserUtils.userLogin(userWithoutEmail);
        response.then().statusCode(SC_UNAUTHORIZED);
        String expectedMessage = "email or password are incorrect";
        response.then().body("message", equalTo(expectedMessage));
    }

    @Test
    @Step("Login impossible without valid password")
    public void loginUserWithoutCorrectPassword(){
        User userWithoutEmail = new User(user.getEmail(), "password", user.getName());

        Response response = UserUtils.userLogin(userWithoutEmail);
        response.then().statusCode(SC_UNAUTHORIZED);
        String expectedMessage = "email or password are incorrect";
        response.then().body("message", equalTo(expectedMessage));
    }


    @After
    @Step("Deleting the created user from the database")
    public void tearDown(){
        String accessToken = UserUtils.getAccessToken(user);
        UserUtils.userDelete(accessToken);
    }
}

