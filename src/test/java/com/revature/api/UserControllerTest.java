package com.revature.api;

import com.revature.controller.UserController;
import com.revature.models.User;
import com.revature.models.UsernamePasswordAuthentication;
import com.revature.repository.UserDao;
import com.revature.service.UserService;
import com.revature.utilities.ConnectionUtil;
import com.revature.utilities.RequestMapper;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {
    private Javalin app;

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
        resetDatabase();

        app = Javalin.create();
        RequestMapper.setUpEndPoints(app);
    }

    @AfterEach
    public void cleanup() {
        resetDatabase();
    }

    @ParameterizedTest
    @DisplayName("API::UserService::authenticate - Success")
    @Order(0)
    @CsvSource({
            "username,password",
            "test,12345"
    })
    public void authenticateSuccess(String username, String password) {
        //Populate DB
        try (Connection connection = ConnectionUtil.createConnection()) {
            PreparedStatement ps = connection.prepareStatement("insert into users (username, password) values (?, ?)");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
        } catch(SQLException e) {
            Assertions.fail("authenticate failed to populate database due to a SQLException.");
        }

        JavalinTest.test(app, (server, client) -> {
            Map<String, String> requestJson = new HashMap<String, String>();
            requestJson.put("username", username);
            requestJson.put("password", password);

            int actualStatusCode;
            try(Response response = client.post("/login", requestJson)) {
                actualStatusCode = response.code();
                Assertions.assertEquals(202, actualStatusCode);
            }
        });
    }

    @Test
    @DisplayName("API::UserService::authenticate - Failure - Username Not Found")
    @Order(1)
    public void authenticateUsernameNotFound() {
        String username = "wrongUsername";
        String password = "password";

        JavalinTest.test(app, (server, client) -> {
            Map<String, String> requestJson = new HashMap<String, String>();
            requestJson.put("username", username);
            requestJson.put("password", password);

            int actualStatusCode;
            try(Response response = client.post("/login", requestJson)) {
                actualStatusCode = response.code();
                Assertions.assertEquals(400, actualStatusCode);
            }
        });
    }

    @Test
    @DisplayName("API::UserService::authenticate - Failure - Wrong Password")
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
            Assertions.fail("authenticate failed to populate database due to a SQLException.");
        }

        //Create an intentionally wrong password
        String wrongPassword = password;
        if (wrongPassword.length() + 1 <= 30) {
            wrongPassword += "a";
        }
        else {
            wrongPassword = wrongPassword.substring(0, 29);
        }

        String wrongPasswordInput = wrongPassword;
        JavalinTest.test(app, (server, client) -> {
            Map<String, String> requestJson = new HashMap<String, String>();
            requestJson.put("username", username);
            requestJson.put("password", wrongPasswordInput);

            int actualStatusCode;
            try(Response response = client.post("/login", requestJson)) {
                actualStatusCode = response.code();
                Assertions.assertEquals(400, actualStatusCode);
            }
        });
    }

    @ParameterizedTest
    @DisplayName("API::UserService::register - Success")
    @Order(3)
    @CsvSource({
            "newUser,password",
            "test123,123456"
    })
    public void registerSuccess(String username, String password) {
        JavalinTest.test(app, (server, client) -> {
            Map<String, String> requestJson = new HashMap<String, String>();
            requestJson.put("username", username);
            requestJson.put("password", password);

            int actualStatusCode;
            String responseBody;
            try(Response response = client.post("/register", requestJson)) {
                actualStatusCode = response.code();
                responseBody = Objects.requireNonNull(response.body().string());

                Assertions.assertEquals(201, actualStatusCode);
            }
        });
    }

    @Test
    @DisplayName("API::UserService::register - Failure - Existing Username")
    @Order(4)
    public void registerExisting() {
        String username = "existingUser";
        String password = "password";

        try (Connection connection = ConnectionUtil.createConnection()) {
            PreparedStatement ps = connection.prepareStatement("insert into users (username, password) values (?, ?)");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
        } catch(SQLException e) {
            Assertions.fail("register failed to populate database due to a SQLException.");
        }

        JavalinTest.test(app, (server, client) -> {
            Map<String, String> requestJson = new HashMap<String, String>();
            requestJson.put("username", username);
            requestJson.put("password", password);

            int actualStatusCode;
            try(Response response = client.post("/register", requestJson)) {
                actualStatusCode = response.code();
                Assertions.assertEquals(400, actualStatusCode);
            }
        });
    }

    @ParameterizedTest
    @DisplayName("API::UserService::register - Failure - Exceed Length")
    @Order(5)
    @CsvSource({
            "moreThan30Characters00000000000,password",
            "username,moreThan30Characters00000000000",
            "moreThan30Characters00000000000,moreThan30Characters00000000000"
    })
    public void registerMaxLength(String username, String password) {
        JavalinTest.test(app, (server, client) -> {
            Map<String, String> requestJson = new HashMap<String, String>();
            requestJson.put("username", username);
            requestJson.put("password", password);

            int actualStatusCode;
            try(Response response = client.post("/register", requestJson)) {
                actualStatusCode = response.code();
                Assertions.assertEquals(400, actualStatusCode);
            }
        });
    }

    @Test
    @DisplayName("API::UserService::register - Failure - Empty Username")
    @Order(6)
    public void registerEmptyUsername() {
        String username = "";
        String password = "password";

        JavalinTest.test(app, (server, client) -> {
            Map<String, String> requestJson = new HashMap<String, String>();
            requestJson.put("username", username);
            requestJson.put("password", password);

            int actualStatusCode;
            try(Response response = client.post("/register", requestJson)) {
                actualStatusCode = response.code();
                Assertions.assertEquals(400, actualStatusCode);
            }
        });
    }

    @Test
    @DisplayName("API::UserService::register - Failure - Empty Password")
    @Order(7)
    public void registerEmptyPassword() {
        String username = "username";
        String password = "";

        JavalinTest.test(app, (server, client) -> {
            Map<String, String> requestJson = new HashMap<String, String>();
            requestJson.put("username", username);
            requestJson.put("password", password);

            int actualStatusCode;
            try(Response response = client.post("/register", requestJson)) {
                actualStatusCode = response.code();
                Assertions.assertEquals(400, actualStatusCode);
            }
        });
    }

    @Test
    @DisplayName("API::UserService::register - Failure - Empty")
    @Order(8)
    public void registerEmpty() {
        String username = "";
        String password = "";

        JavalinTest.test(app, (server, client) -> {
            Map<String, String> requestJson = new HashMap<String, String>();
            requestJson.put("username", username);
            requestJson.put("password", password);

            int actualStatusCode;
            try(Response response = client.post("/register", requestJson)) {
                actualStatusCode = response.code();
                Assertions.assertEquals(400, actualStatusCode);
            }
        });
    }
}
