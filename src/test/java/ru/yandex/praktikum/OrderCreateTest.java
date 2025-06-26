package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import io.restassured.response.Response;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class OrderCreateTest {
    private User user;

    @Before
    @Step("Setup base URL and create new user")
    public void setUp() {
        Utils.setUp();
        user = UserGenerator.generateUser();
        UserUtils.userCreate(user);
    }

    @Test
    @DisplayName("Сreating order by authorized user")
    public void creatingOrderByAuthorizedUserTest() {
        String accessToken = UserUtils.getAccessToken(user);

        Response response = OrderUtils.createOrderWithValidDate(accessToken);
        response.then().statusCode(SC_OK);
        response.then().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Сreating order by unauthorized user")
    public void creatingOrderByUnauthorizedUserTest() {
        Response response = OrderUtils.createOrderWithValidDate("");
        response.then().statusCode(SC_OK);
        response.then().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Creating order without ingredients by authorized user")
    public void creatingOrderWithoutIngredientsByAuthorizedUserTest() {
        String accessToken = UserUtils.getAccessToken(user);

        String[] ingredients = {};
        Order order = new Order(ingredients);
        Response response = OrderUtils.orderCreate(order, accessToken);
        response.then().statusCode(SC_BAD_REQUEST);
        response.then().body("success", equalTo(false));
        response.then().body("message", equalTo(ErrorsMessages.INGREDIENTS_ABSENT));
    }

    @Test
    @DisplayName("Creating order without ingredients by unauthorized user")
    public void creatingOrderWithoutIngredientsByUnauthorizedUserTest() {
        String[] ingredients = {};
        Order order = new Order(ingredients);

        Response response = OrderUtils.orderCreate(order, "");
        response.then().statusCode(SC_BAD_REQUEST);
        response.then().body("success", equalTo(false));
        response.then().body("message", equalTo(ErrorsMessages.INGREDIENTS_ABSENT));
    }

    @Test
    @DisplayName("Creating order with invalid ingredients id by authorized user")
    public void creatingOrderWithInvalidIngredientsByAuthorizedUserTest() {
        String accessToken = UserUtils.getAccessToken(user);

        String[] ingredients = {"0000", "0000"};
        Order order = new Order(ingredients);
        Response response = OrderUtils.orderCreate(order, accessToken);
        response.then().statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Creating order with invalid ingredients id by unauthorized user")
    public void creatingOrderWithInvalidIngredientsByUnauthorizedUserTest() {
        String[] ingredients = {"0000", "0000"};
        Order order = new Order(ingredients);

        Response response = OrderUtils.orderCreate(order, "");
        response.then().statusCode(SC_INTERNAL_SERVER_ERROR);
    }


    @After
    @Step("Deleting the created user from the database")
    public void tearDown(){
        String accessToken = UserUtils.getAccessToken(user);
        UserUtils.userDelete(accessToken);
    }
    }

