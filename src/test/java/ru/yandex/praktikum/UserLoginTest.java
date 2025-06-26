package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserLoginTest {
    private User user;
    private Faker faker;

    @Before
    @Step("Setup base URL and create new user")
    public void setUp() {
        Utils.setUp();
        user = UserGenerator.generateUser();
        UserUtils.userCreate(user);
        faker = new Faker();
    }

    @Test
    @DisplayName("Login user with valid date")
    public void loginUserUserWithValidDateTest(){
        Response response = UserUtils.userLogin(user);
        response.then().statusCode(SC_OK);
        response.then().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Login impossible without valid email")
    public void loginUserWithoutCorrectEmailTest(){
        String newEmail = faker.internet().emailAddress();
        User userWithoutCorrectEmail = new User(newEmail, user.getPassword(), user.getName());
        Response response = UserUtils.userLogin(userWithoutCorrectEmail);
        response.then().statusCode(SC_UNAUTHORIZED);
        response.then().body("message", equalTo(ErrorsMessages.INCORRECT_EMAIL_OR_PASSWORD_MESSAGE));
    }

    @Test
    @DisplayName("Login impossible without valid password")
    public void loginUserWithoutCorrectPasswordTest(){
        String newPassword = faker.internet().password();
        User userWithoutCorrectPassword = new User(user.getEmail(), newPassword, user.getName());

        Response response = UserUtils.userLogin(userWithoutCorrectPassword);
        response.then().statusCode(SC_UNAUTHORIZED);
        response.then().body("message", equalTo(ErrorsMessages.INCORRECT_EMAIL_OR_PASSWORD_MESSAGE));
    }


    @After
    @Step("Deleting the created user from the database")
    public void tearDown(){
        String accessToken = UserUtils.getAccessToken(user);
        UserUtils.userDelete(accessToken);
    }
}

