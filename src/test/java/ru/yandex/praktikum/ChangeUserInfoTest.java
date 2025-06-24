package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class ChangeUserInfoTest {
    private User user;
    private String accessToken;
    private String accessTokenForUserWithEmail;

    @Before
    @Step("Setup base URL and create new user")
    public void setUp() {
        Utils.setUp();
        user = new User("frodo@frodo.com", "frodotest", "Frodo");
        UserUtils.userCreate(user);
    }

    @Test
    @Step("Change user name with authorization")
    public void changeUserNameWithAuthorization(){
        Response response = UserUtils.userLogin(user);
        accessToken = UserUtils.getAccessToken(user);
        String newName = "Aragorn";
        User userWithNewName = new User(user.getEmail(), user.getPassword(), newName);
        Response updateResponse = UserUtils.updateUserInfo(accessToken, userWithNewName);
        updateResponse.then().statusCode(SC_OK);
        Response newUserNameResponce = UserUtils.userLogin(userWithNewName);
        newUserNameResponce.then().assertThat().body("user.name", equalTo(newName));
    }

    @Test
    @Step("Change user email with authorization")
    public void changeUserEmailWithAuthorization(){
        Response response = UserUtils.userLogin(user);
        accessToken = UserUtils.getAccessToken(user);
        String newEmail = "aragorn@aragorn.java";
        User userWithNewEmail = new User(newEmail, user.getPassword(), user.getName());
        Response updateResponse = UserUtils.updateUserInfo(accessToken, userWithNewEmail);
        updateResponse.then().statusCode(SC_OK);
        Response newUserNameResponce = UserUtils.userLogin(userWithNewEmail);
        newUserNameResponce.then().assertThat().body("user.email", equalTo(newEmail));
    }

    @Test
    @Step("Change user email, that is already in use, impossible with authorization")
    public void changeUserEmailAlreadyInUse(){
        User userWithEmail = new User("aragorn@aragorn.com", "aragorntest", "Aragorn");
        UserUtils.userCreate(userWithEmail);
        accessTokenForUserWithEmail = UserUtils.getAccessToken(userWithEmail);
        Response response = UserUtils.userLogin(user);
        accessToken = UserUtils.getAccessToken(user);
        User userWithNewEmail = new User(userWithEmail.getEmail(), user.getPassword(), user.getName());
        Response updateResponse = UserUtils.updateUserInfo(accessToken, userWithNewEmail);
        updateResponse.then().statusCode(SC_FORBIDDEN);
        String expectedMessage = "User with such email already exists";
        updateResponse.then().body("message", equalTo(expectedMessage));
        //В данном методе использую удаление пользователя внутри метода, поскольку для этого единственного метода требуется создание двух пользователей.
        //один пользователь будет удален в @After, как и для всех тестов, а второй - внутри тестового метода
        UserUtils.userDelete(accessTokenForUserWithEmail);
    }

    @Test
    @Step("Change user name impossible without authorization")
    public void changeUserNameWithoutAuthorization(){
        Response response = UserUtils.userLogin(user);
        accessToken = UserUtils.getAccessToken(user);
        String newName = "Aragorn";
        User userWithNewName = new User(user.getEmail(), user.getPassword(), newName);
        Response updateResponse = UserUtils.updateUserInfo("", userWithNewName);
        updateResponse.then().statusCode(SC_UNAUTHORIZED);
        String expectedMessage = "You should be authorised";
        updateResponse.then().body("message", equalTo(expectedMessage));
    }

    @Test
    @Step("Change user email impossible without authorization")
    public void changeUserEmalWithoutAuthorization(){
        Response response = UserUtils.userLogin(user);
        accessToken = UserUtils.getAccessToken(user);
        String newEmail = "aragorn@aragorn.java";
        User userWithNewEmail = new User(newEmail, user.getPassword(), user.getName());
        Response updateResponse = UserUtils.updateUserInfo("", userWithNewEmail);
        updateResponse.then().statusCode(SC_UNAUTHORIZED);
        String expectedMessage = "You should be authorised";
        updateResponse.then().body("message", equalTo(expectedMessage));
    }

    @After
    @Step("Deleting the created user from the database")
    public void tearDown(){
        UserUtils.userDelete(accessToken);
    }
}
