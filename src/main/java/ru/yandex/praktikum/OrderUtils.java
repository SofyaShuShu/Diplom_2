package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class OrderUtils {
    private String firstIngredientId;
    private String secondIngredientId;

    @Step("Method for order create")
    public static Response orderCreate(Order order, String accessToken){
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .header("Authorization", accessToken)
                        .body(order)
                        .when()
                        .post(Urls.ORDER_PATH);
        return response;
    }

    @Step("Method for order create with valid date")
    public static Response createOrderWithValidDate(String accessToken){
        String[] ingredients = {getRandomIngredientId(), getRandomIngredientId()};
        Order order = new Order(ingredients);
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .header("Authorization", accessToken)
                        .body(order)
                        .when()
                        .post(Urls.ORDER_PATH);
        return response;
    }

    @Step("Method for get random ingredient id")
    public static String getRandomIngredientId() {
        Response response = given()
                .when()
                .get(Urls.INGREDIENTS_PATH);
        List<Map<String, Object>> ingredients = response.jsonPath().getList("data");
        Random random = new Random();
        int randomIndex = random.nextInt(ingredients.size());

        String randomIngredientId = (String) ingredients.get(randomIndex).get("_id");
        return randomIngredientId;
    }
}

