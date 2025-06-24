package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserUtils {
    @Step("Method for user create")
    public static Response userCreate(User user){
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(user)
                        .when()
                        .post("/api/auth/register");
        return response;
    }

    @Step("Method for get user access token")
    public static String getAccessToken(User user){
        String accessToken =
                given()
               .header("Content-Type", "application/json")
               .body(user)
               .post("api/auth/login")
               .then()
               .extract()
               .path("accessToken");
        return accessToken;

    }

    @Step("Method for user delete")
    public static void userDelete(String accessToken) {
        if (accessToken != null) {
            Response response =
                    given()
                            .header("Authorization", accessToken)
                            .when()
                            .delete("/api/auth/user");
        }
    }


    @Step("Method for user login")
    public static Response userLogin(User user){
        Response response = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/login");
        return response;
    }

    @Step("Method for updating user info")
    public static Response updateUserInfo(String accessToken, User user) {
        Response response =
                given()
                        .header("Authorization", accessToken)
                        .header("Content-Type", "application/json")
                        .body(user)
                        .when()
                        .patch("/api/auth/user");
        return response;
    }

    @Step("Method for get user orders")
    public static Response getUserOrder(String accessToken){
    Response response = given()
            .header("Authorization", accessToken)
            .when()
            .get("/api/orders");
        return response;
    }
}
