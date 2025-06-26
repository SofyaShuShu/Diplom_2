package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;

public class GetUserOrdersTest {
    private String accessToken;
    private User user;

    @Before
    @Step("Setup base URL and create new user")
    public void setUp() {
        Utils.setUp();
        user = UserGenerator.generateUser();
        UserUtils.userCreate(user);
        accessToken = UserUtils.getAccessToken(user);
    }

    @DisplayName("Method checks whether an authorized user can retrieve a list of orders.")
    @Test
    public void testGetOrdersAuthenticatedUserTest() {
        Response response = UserUtils.getUserOrder(accessToken);
        response.then().statusCode(SC_OK);
        response.then().body("success", equalTo(true));
    }

    @DisplayName("Method checks that an unauthorized user cannot get the order list.")
    @Test
    public void testGetOrdersUnauthenticatedUserTest() {
        Response response = UserUtils.getUserOrder("");
        response.then().statusCode(SC_UNAUTHORIZED);
        response.then().body("success", equalTo(false));
        response.then().body("message", equalTo(ErrorsMessages.UNAUTHORISED_ERROR_MESSAGE));
    }

    @After
    @Step("Deleting the created user from the database")
    public void tearDown() {
        UserUtils.userDelete(accessToken);
    }
}

