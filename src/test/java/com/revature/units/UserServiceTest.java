package com.revature.units;

import com.revature.models.User;
import com.revature.models.UsernamePasswordAuthentication;
import com.revature.repository.UserDao;
import com.revature.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {

    @Mock
    private UserDao dao;

    private UserService userService;

    @BeforeEach
    public void setup() {
        dao = Mockito.mock(UserDao.class);
        userService = new UserService(dao);
    }

    @AfterEach
    public void cleanup() {
        dao = null;
        userService = null;
    }

    @ParameterizedTest
    @DisplayName("UserService::authenticate - Success")
    @Order(0)
    @CsvSource({
            "username,password",
            "test,12345"
    })
    public void authenticateValid(String username, String password) {
        UsernamePasswordAuthentication loginRequestData = new UsernamePasswordAuthentication();
        loginRequestData.setUsername(username);
        loginRequestData.setPassword(password);

        User toReturn = new User();
        toReturn.setUsername(username);
        toReturn.setPassword(password);

        when(dao.getUserByUsername(username)).thenReturn(toReturn);

        User actual = userService.authenticate(loginRequestData);

        boolean credentialsMatch = actual.getUsername().equals(username) && actual.getPassword().equals(password);
        Assertions.assertTrue(credentialsMatch);
    }

    @ParameterizedTest
    @DisplayName("UserService::authenticate - Failure -Username Not Found")
    @Order(1)
    @CsvSource({
            "wrongUsername,password"
    })
    public void authenticateUsernameNotFound(String username, String password) {
        UsernamePasswordAuthentication loginRequestData = new UsernamePasswordAuthentication();
        loginRequestData.setUsername(username);
        loginRequestData.setPassword(password);

        when(dao.getUserByUsername(username)).thenReturn(null);

        User actual = userService.authenticate(loginRequestData);
        Assertions.assertNull(actual);
    }

    @ParameterizedTest
    @DisplayName("UserService::authenticate - Failure - Wrong Password")
    @Order(2)
    @CsvSource({
            "username,wrongPassword"
    })
    public void authenticateWrongPassword(String username, String password) {
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

        User toReturn = new User();
        toReturn.setUsername(username);
        toReturn.setPassword(password);

        when(dao.getUserByUsername(username)).thenReturn(toReturn);

        User actual = userService.authenticate(loginRequestData);
        Assertions.assertNull(actual);
    }

    @ParameterizedTest
    @DisplayName("UserService::register - Success")
    @Order(3)
    @CsvSource({
            "newUser,password",
            "test123,123456"
    })
    public void registerSuccess(String username, String password) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);

        UsernamePasswordAuthentication auth = new UsernamePasswordAuthentication();
        auth.setUsername(username);
        auth.setPassword(password);

        User returnedUser = new User();
        returnedUser.setUsername(username);
        returnedUser.setPassword(password);
        when(dao.getUserByUsername(username)).thenReturn(null);
        when(dao.createUser(auth)).thenReturn(returnedUser);

        User actual = userService.register(newUser);
        boolean credentialsMatch = actual.getUsername().equals(username) && actual.getPassword().equals(password);
        Assertions.assertTrue(credentialsMatch);
    }

    @ParameterizedTest
    @DisplayName("UserService::register - Failure - Existing Username")
    @Order(4)
    @CsvSource({
            "existingUser,password"
    })
    public void registerExisting(String username, String password) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);

        UsernamePasswordAuthentication auth = new UsernamePasswordAuthentication();
        auth.setUsername(username);
        auth.setPassword(password);

        User returnedUser = new User();
        returnedUser.setUsername(username);
        returnedUser.setPassword(password);
        when(dao.createUser(auth)).thenReturn(returnedUser);
        when(dao.getUserByUsername(username)).thenReturn(returnedUser);

        User actual = userService.register(newUser);
        Assertions.assertNull(actual);
    }

    @ParameterizedTest
    @DisplayName("UserService::register - Failure - Exceed Length")
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
    @DisplayName("UserService::register - Failure - Empty Username")
    @Order(6)
    public void registerEmptyUsername() {
        User newUser = new User();
        newUser.setUsername("");
        newUser.setPassword("password");

        User actual = userService.register(newUser);
        Assertions.assertNull(actual);
    }

    @Test
    @DisplayName("UserService::register - Failure - Empty Password")
    @Order(7)
    public void registerEmptyPassword() {
        User newUser = new User();
        newUser.setUsername("username");
        newUser.setPassword("");

        User actual = userService.register(newUser);
        Assertions.assertNull(actual);
    }

    @Test
    @DisplayName("UserService::register - Failure - Empty")
    @Order(8)
    public void registerEmpty() {
        User newUser = new User();
        newUser.setUsername("");
        newUser.setPassword("");

        User actual = userService.register(newUser);
        Assertions.assertNull(actual);
    }
}
