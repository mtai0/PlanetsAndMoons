package com.revature.integration.databasedao;

import com.revature.models.User;
import com.revature.models.UsernamePasswordAuthentication;
import com.revature.repository.UserDao;
import com.revature.utilities.ConnectionUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDaoIntegrationTest {
    private UserDao dao;

    //Will be used for test cases that need the user ID
    private int getUserId(Connection connection, String name) {
        int id = -1;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT id FROM users WHERE username = ?;");
            ps.setString(1, name);

            ResultSet results = ps.executeQuery();
            if (results.next()){
                id = results.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("getUserId: Failed to get user ID.");
        }
        return id;
    }

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
    public void setupTest(){
        dao = new UserDao();
        resetDatabase();
    }

    @AfterEach
    public void cleanupTest(){
        dao = null;
        resetDatabase();
    }

    @ParameterizedTest
    @DisplayName("Integration::UserDao-Database::createUser - Success")
    @Order(0)
    @CsvSource({
            "username,password",
            "test,testpassword",
            "user3,12345"
    })
    public void createUserSuccess(String username, String password){
        UsernamePasswordAuthentication auth = new UsernamePasswordAuthentication();
        auth.setUsername(username);
        auth.setPassword(password);

        User actual = dao.createUser(auth);

        if (actual != null){
            //Verify username and password matches.
            boolean match = username.equals(actual.getUsername()) && password.equals(actual.getPassword());
            Assertions.assertTrue(match);
        }
        else {
            Assertions.fail("createUser returned null");
        }
    }

    @ParameterizedTest
    @DisplayName("Integration::UserDao-Database::getUserByUsername - Success")
    @Order(1)
    @CsvSource({
            "username",
            "123",
            "user3"
    })
    public void getUserByUsernameSuccess(String username) {
        String password = "placeholder";
        //Populate database
        try (Connection connection = ConnectionUtil.createConnection()) {
            PreparedStatement ps = connection.prepareStatement("insert into users (username, password) values (?, ?)");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
        } catch(SQLException e) {
            Assertions.fail("getUserByUsername failed to populate database due to a SQLException.");
        }

        User actual = dao.getUserByUsername(username);
        if (actual != null){
            Assertions.assertEquals(username, actual.getUsername());
        }
        else {
            Assertions.fail("getUserByUsername returned null");
        }
    }

    @Test
    @DisplayName("Integration::UserDao-Database::getUserByUsername - Failure")
    @Order(2)
    public void getUserByUsernameSuccess() {
        User actual = dao.getUserByUsername("username");
        Assertions.assertNull(actual);
    }
}
