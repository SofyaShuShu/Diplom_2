package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class ChangeUserInfoTest {
    private User user;
    private String accessToken;
    private String accessTokenForUserWithEmailForChange;
    private User userWithEmailForChange;
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
    @DisplayName("Change user name with authorization")
    public void changeUserNameWithAuthorizationTest(){
        Response response = UserUtils.userLogin(user);
        accessToken = UserUtils.getAccessToken(user);
        String newName = faker.name().firstName();
        User userWithNewName = new User(user.getEmail(), user.getPassword(), newName);
        Response updateResponse = UserUtils.updateUserInfo(accessToken, userWithNewName);
        updateResponse.then().statusCode(SC_OK);
        Response newUserNameResponce = UserUtils.userLogin(userWithNewName);
        newUserNameResponce.then().assertThat().body("user.name", equalTo(newName));
    }

    @Test
    @DisplayName("Change user email with authorization")
    public void changeUserEmailWithAuthorizationTest(){
        Response response = UserUtils.userLogin(user);
        accessToken = UserUtils.getAccessToken(user);
        String newEmail = faker.internet().emailAddress();
        User userWithNewEmail = new User(newEmail, user.getPassword(), user.getName());
        Response updateResponse = UserUtils.updateUserInfo(accessToken, userWithNewEmail);
        updateResponse.then().statusCode(SC_OK);
        Response newUserNameResponce = UserUtils.userLogin(userWithNewEmail);
        newUserNameResponce.then().assertThat().body("user.email", equalTo(newEmail));
    }

    @Test
    @DisplayName("Change user email, that is already in use, impossible with authorization")
    public void changeUserEmailAlreadyInUseTest(){
        userWithEmailForChange = UserGenerator.generateUser();
        UserUtils.userCreate(userWithEmailForChange);
        accessTokenForUserWithEmailForChange = UserUtils.getAccessToken(userWithEmailForChange);
        Response response = UserUtils.userLogin(user);
        accessToken = UserUtils.getAccessToken(user);

        User userWithChangeEmail = new User(userWithEmailForChange.getEmail(), user.getPassword(), user.getName());
        Response updateResponse = UserUtils.updateUserInfo(accessToken, userWithChangeEmail);
        updateResponse.then().statusCode(SC_FORBIDDEN);
        updateResponse.then().body("message", equalTo(ErrorsMessages.EMAIL_EXISTS_MESSAGE));
    }

    @Test
    @DisplayName("Change user name impossible without authorization")
    public void changeUserNameWithoutAuthorizationTest(){
        Response response = UserUtils.userLogin(user);
        accessToken = UserUtils.getAccessToken(user);
        String newName = faker.name().firstName();
        User userWithNewName = new User(user.getEmail(), user.getPassword(), newName);
        Response updateResponse = UserUtils.updateUserInfo("", userWithNewName);
        updateResponse.then().statusCode(SC_UNAUTHORIZED);
        updateResponse.then().body("message", equalTo(ErrorsMessages.UNAUTHORISED_ERROR_MESSAGE));
    }

    @Test
    @DisplayName ("Change user email impossible without authorization")
    public void changeUserEmailWithoutAuthorizationTest(){
        Response response = UserUtils.userLogin(user);
        accessToken = UserUtils.getAccessToken(user);
        String newEmail = faker.internet().emailAddress();
        User userWithNewEmail = new User(newEmail, user.getPassword(), user.getName());
        Response updateResponse = UserUtils.updateUserInfo("", userWithNewEmail);
        updateResponse.then().statusCode(SC_UNAUTHORIZED);
        updateResponse.then().body("message", equalTo(ErrorsMessages.UNAUTHORISED_ERROR_MESSAGE));
    }

    @After
    @Step("Deleting the created user from the database")
    public void tearDown(){
        UserUtils.userDelete(accessToken);
        if(accessTokenForUserWithEmailForChange != null){
            UserUtils.userDelete(accessTokenForUserWithEmailForChange);
        }
        }
    }
