package ru.yandex.praktikum;


import net.datafaker.Faker;

public class UserGenerator {
    private static final Faker FAKER = new Faker();


    public static User generateUser() {
        return new User(
                FAKER.internet().emailAddress(),
                FAKER.internet().password(),
                FAKER.name().firstName()
        );
    }

    public static User generateUserWithoutEmail() {
        return new User(
                null,
                FAKER.internet().password(),
                FAKER.name().firstName()
        );
    }

    public static User generateUserWithoutPassword() {
        return new User(
                FAKER.internet().emailAddress(),
                null,
                FAKER.name().firstName()
        );
    }

    public static User generateUserWithoutName() {
        return new User(
                FAKER.internet().emailAddress(),
                FAKER.internet().password(),
                null
        );
    }
}