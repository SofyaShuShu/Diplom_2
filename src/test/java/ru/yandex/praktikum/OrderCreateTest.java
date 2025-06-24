package ru.yandex.praktikum;

import io.qameta.allure.Step;
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
        user = new User("frodo@frodo.com", "frodotest", "Frodo");
        UserUtils.userCreate(user);
    }

    @Test
    @Step("Сreating order by authorized user")
    public void creatingOrderByAuthorizedUser() {
        String accessToken = UserUtils.getAccessToken(user);

        Response response = OrderUtils.createOrderWithValidDate(accessToken);
        response.then().statusCode(SC_OK);
        response.then().body("success", equalTo(true));
    }

    @Test
    @Step("Сreating order by unauthorized user")
    public void creatingOrderByUnauthorizedUser() {
        Response response = OrderUtils.createOrderWithValidDate("");
        response.then().statusCode(SC_OK);
        response.then().body("success", equalTo(true));
    }

    @Test
    @Step("Creating order without ingredients by authorized user")
    public void creatingOrderWithoutIngredientsByAuthorizedUser() {
        String accessToken = UserUtils.getAccessToken(user);

        String[] ingredients = {};
        Order order = new Order(ingredients);
        Response response = OrderUtils.orderCreate(order, accessToken);
        response.then().statusCode(SC_BAD_REQUEST);
        response.then().body("success", equalTo(false));
        response.then().body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @Step("Creating order without ingredients by unauthorized user")
    public void creatingOrderWithoutIngredientsByUnauthorizedUser() {
        String[] ingredients = {};
        Order order = new Order(ingredients);

        Response response = OrderUtils.orderCreate(order, "");
        response.then().statusCode(SC_BAD_REQUEST);
        response.then().body("success", equalTo(false));
        response.then().body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @Step("Creating order with invalid ingredients id by authorized user")
    public void creatingOrderWithInvalidIngredientsByAuthorizedUser() {
        String accessToken = UserUtils.getAccessToken(user);

        String[] ingredients = {"0000", "0000"};
        Order order = new Order(ingredients);
        Response response = OrderUtils.orderCreate(order, accessToken);
        response.then().statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @Step("Creating order with invalid ingredients id by unauthorized user")
    public void creatingOrderWithInvalidIngredientsByUnauthorizedUser() {
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

