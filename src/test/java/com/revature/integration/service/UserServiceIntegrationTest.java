package com.revature.integration.service;

import com.revature.models.User;
import com.revature.models.UsernamePasswordAuthentication;
import com.revature.repository.UserDao;
import com.revature.service.UserService;
import com.revature.utilities.ConnectionUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceIntegrationTest {
    private UserDao dao;
    private UserService userService;

    private void resetDatabase(){
        try (Connection connection = ConnectionUtil.createConnection()) {
            PreparedStatement psMoon = connection.prepareStatement("DELETE FROM moons;");
            psMoon.executeUpdate();

            PreparedStatement psPlanet = connection.prepareStatement("DELETE FROM planets;");
            psPlanet.executeUpdate();

            PreparedStatement psUser = connection.prepareStatement("DELETE FROM users;");
            psUser.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void setup() {
        dao = new UserDao();
        userService = new UserService(dao);
        resetDatabase();
    }

    @AfterEach
    public void cleanup() {
        dao = null;
        userService = null;
        resetDatabase();
    }

    @ParameterizedTest
    @DisplayName("Integration::UserService::authenticate - Success")
    @Order(0)
    @CsvSource({
            "username,password",
            "test,12345"
    })
    public void authenticateValid(String username, String password) {
        try (Connection connection = ConnectionUtil.createConnection()) {
            PreparedStatement ps = connection.prepareStatement("insert into users (username, password) values (?, ?)");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
        } catch(SQLException e) {
            Assertions.fail("authenticateValid failed to populate database due to a SQLException.");
        }

        UsernamePasswordAuthentication loginRequestData = new UsernamePasswordAuthentication();
        loginRequestData.setUsername(username);
        loginRequestData.setPassword(password);

        User actual = userService.authenticate(loginRequestData);

        boolean credentialsMatch = actual.getUsername().equals(username) && actual.getPassword().equals(password);
        Assertions.assertTrue(credentialsMatch);
    }

    @Test
    @DisplayName("Integration::UserService::authenticate - Failure - Username Not Found")
    @Order(1)
    public void authenticateUsernameNotFound() {
        String username = "wrongUsername";
        String password = "password";
        UsernamePasswordAuthentication loginRequestData = new UsernamePasswordAuthentication();
        loginRequestData.setUsername(username);
        loginRequestData.setPassword(password);

        User actual = userService.authenticate(loginRequestData);
        Assertions.assertNull(actual);
    }

    @Test
    @DisplayName("Integration::UserService::authenticate - Failure - Wrong Password")
    @Order(2)
    public void authenticateWrongPassword() {
        String username = "username";
        String password = "password";

        try (Connection connection = ConnectionUtil.createConnection()) {
            PreparedStatement ps = connection.prepareStatement("insert into users (username, password) values (?, ?)");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
        } catch(SQLException e) {
            Assertions.fail("authenticateWrongPassword failed to populate database due to a SQLException.");
        }

        //Create an intentionally wrong password
        String wrongPassword = password;
        if (wrongPassword.length() + 1 <= 30) {
            wrongPassword += "a";
        }
        else {
            wrongPassword = wrongPassword.substring(0, 29);
        }

        UsernamePasswordAuthentication loginRequestData = new UsernamePasswordAuthentication();
        loginRequestData.setUsername(username);
        loginRequestData.setPassword(wrongPassword);

        User actual = userService.authenticate(loginRequestData);
        Assertions.assertNull(actual);
    }

    @ParameterizedTest
    @DisplayName("Integration::UserService::register - Success")
    @Order(3)
    @CsvSource({
            "newUser,password",
            "test123,123456"
    })
    public void registerSuccess(String username, String password) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);

        User actual = userService.register(newUser);
        boolean credentialsMatch = actual.getUsername().equals(username) && actual.getPassword().equals(password);
        Assertions.assertTrue(credentialsMatch);
    }

    @ParameterizedTest
    @DisplayName("Integration::UserService::register - Failure - Existing Username")
    @Order(4)
    @CsvSource({
            "existingUser,password"
    })
    public void registerExisting(String username, String password) {

        try (Connection connection = ConnectionUtil.createConnection()) {
            PreparedStatement ps = connection.prepareStatement("insert into users (username, password) values (?, ?)");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
        } catch(SQLException e) {
            Assertions.fail("registerExisting failed to populate database due to a SQLException.");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);

        User actual = userService.register(newUser);
        Assertions.assertNull(actual);
    }

    @ParameterizedTest
    @DisplayName("Integration::UserService::register - Failure - Exceed Length")
    @Order(5)
    @CsvSource({
            "moreThan30Characters00000000000,password",
            "username,moreThan30Characters00000000000",
            "moreThan30Characters00000000000,moreThan30Characters00000000000"
    })
    public void registerMaxLength(String username, String password) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);

        User actual = userService.register(newUser);
        Assertions.assertNull(actual);
    }

    @Test
    @DisplayName("Integration::UserService::register - Failure - Empty Username")
    @Order(6)
    public void registerEmptyUsername() {
        User newUser = new User();
        newUser.setUsername("");
        newUser.setPassword("password");

        User actual = userService.register(newUser);
        Assertions.assertNull(actual);
    }

    @Test
    @DisplayName("Integration::UserService::register - Failure - Empty Password")
    @Order(7)
    public void registerEmptyPassword() {
        User newUser = new User();
        newUser.setUsername("username");
        newUser.setPassword("");

        User actual = userService.register(newUser);
        Assertions.assertNull(actual);
    }

    @Test
    @DisplayName("Integration::UserService::register - Failure - Empty")
    @Order(8)
    public void registerEmpty() {
        User newUser = new User();
        newUser.setUsername("");
        newUser.setPassword("");

        User actual = userService.register(newUser);
        Assertions.assertNull(actual);
    }
}
