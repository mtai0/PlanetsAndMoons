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
    @DisplayName("UserService::authenticate - Valid Input")
    @Order(0)
    @CsvSource({
            "username,password"
    })
    public void authenticateValid(String username, String password) {
        UsernamePasswordAuthentication loginRequestData = new UsernamePasswordAuthentication();
        loginRequestData.setUsername(username);
        loginRequestData.setPassword(password);

        User toReturn = new User();
        toReturn.setUsername(loginRequestData.getUsername());
        toReturn.setPassword(loginRequestData.getPassword());

        when(dao.getUserByUsername(loginRequestData.getUsername())).thenReturn(toReturn);

        User actual = userService.authenticate(loginRequestData);

        boolean credentialsMatch = actual.getUsername().equals(username) && actual.getPassword().equals(password);
        Assertions.assertTrue(credentialsMatch);
    }

    @ParameterizedTest
    @DisplayName("UserService::authenticate - Invalid")
    @Order(1)
    @CsvSource({
            "username, wrongPassword",
            "wrongUsername, password"
    })
    public void authenticateInvalid(String username, String password) {
        UsernamePasswordAuthentication loginRequestData = new UsernamePasswordAuthentication();
        loginRequestData.setUsername(username);
        loginRequestData.setPassword(password);

        when(dao.getUserByUsername(loginRequestData.getUsername())).thenReturn(null);

        User actual = userService.authenticate(loginRequestData);
        Assertions.assertNull(actual);
    }

    @ParameterizedTest
    @DisplayName("UserService::register - Valid Input")
    @Order(2)
    @CsvSource({
            "newUser, password"
    })
    public void registerValid(String username, String password) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);

        UsernamePasswordAuthentication auth = new UsernamePasswordAuthentication();
        auth.setUsername(username);
        auth.setPassword(password);

        User returnedUser = new User();
        returnedUser.setUsername(auth.getUsername());
        returnedUser.setPassword(auth.getPassword());
        when(dao.getUserByUsername(username)).thenReturn(null);
        when(dao.createUser(auth)).thenReturn(returnedUser);

        User actual = userService.register(newUser);
        boolean credentialsMatch = actual.getUsername().equals(username) && actual.getPassword().equals(password);
        Assertions.assertTrue(credentialsMatch);
    }

    @ParameterizedTest
    @DisplayName("UserService::register - Existing Username")
    @Order(3)
    @CsvSource({
            "existingUser, password"
    })
    public void registerExisting(String username, String password) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);

        UsernamePasswordAuthentication auth = new UsernamePasswordAuthentication();
        auth.setUsername(username);
        auth.setPassword(password);

        User returnedUser = new User();
        returnedUser.setUsername(auth.getUsername());
        returnedUser.setPassword(auth.getPassword());
        when(dao.createUser(auth)).thenReturn(returnedUser);
        when(dao.getUserByUsername(username)).thenReturn(returnedUser);

        User actual = userService.register(newUser);
        Assertions.assertNull(actual);
    }

    @ParameterizedTest
    @DisplayName("UserService::register - Exceed Length")
    @Order(4)
    @CsvSource({
            "moreThan30Characters00000000000, password",
            "username, moreThan30Characters00000000000",
            "moreThan30Characters00000000000, moreThan30Characters00000000000"
    })
    public void registerMaxLength(String username, String password) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);

        User actual = userService.register(newUser);
        Assertions.assertNull(actual);
    }

    @Test
    @DisplayName("UserService::register - Empty Username")
    @Order(5)
    public void registerEmptyUsername() {
        User newUser = new User();
        newUser.setUsername("");
        newUser.setPassword("password");

        User actual = userService.register(newUser);
        Assertions.assertNull(actual);
    }

    @Test
    @DisplayName("UserService::register - Empty Password")
    @Order(5)
    public void registerEmptyPassword() {
        User newUser = new User();
        newUser.setUsername("username");
        newUser.setPassword("");

        User actual = userService.register(newUser);
        Assertions.assertNull(actual);
    }

    @Test
    @DisplayName("UserService::register - Empty")
    @Order(6)
    public void registerEmpty() {
        User newUser = new User();
        newUser.setUsername("");
        newUser.setPassword("");

        User actual = userService.register(newUser);
        Assertions.assertNull(actual);
    }
}
