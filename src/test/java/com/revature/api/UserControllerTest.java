package com.revature.api;

import com.revature.controller.UserController;
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
            //String responseBody;
            try(Response response = client.post("/login", requestJson)) {
                actualStatusCode = response.code();
                //responseBody = Objects.requireNonNull(response.body().string());
                Assertions.assertEquals(202, actualStatusCode);
            }
        });
    }
}
