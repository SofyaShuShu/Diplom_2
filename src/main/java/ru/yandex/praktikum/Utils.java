package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.restassured.RestAssured;

public class Utils {
    @Step("Method for setup base URL")
    public static void setUp(){
        RestAssured.baseURI = Urls.MAIN_PAGE_PATH;
    }

}
