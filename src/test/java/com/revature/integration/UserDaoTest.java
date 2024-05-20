package com.revature.integration;

import com.revature.MainDriverTest;
import com.revature.models.User;
import com.revature.models.UsernamePasswordAuthentication;
import com.revature.repository.UserDao;
import com.revature.utilities.ConnectionUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDaoTest {
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

    private void resetUserDatabase(){
        try (Connection connection = ConnectionUtil.createConnection()) {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM users;");
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void setupTest(){
        dao = new UserDao();
        resetUserDatabase();
    }

    @AfterEach
    public void cleanupTest(){
        dao = null;
        resetUserDatabase();
    }

    @ParameterizedTest
    @DisplayName("Integration::UserDao::createUser - Success")
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
    @DisplayName("Integration::UserDao::getUserByUsername - Success")
    @Order(0)
    @CsvSource({
            "username",
            "123",
            "user3"
    })
    public void getUserByUsernameSuccess(String username) {
        //Populate database
        try (Connection connection = ConnectionUtil.createConnection()) {
            PreparedStatement ps = connection.prepareStatement("insert into users (username, password) values (?, ?)");
            ps.setString(1, username);
            ps.setString(2, "placeholderPassword");
            ps.executeUpdate();
        } catch(SQLException e) {
            Assertions.fail("getUserByUSername failed to populate database due to a SQLException.");
        }

        User actual = dao.getUserByUsername(username);
        if (actual != null){
            //Verify username and password matches.
            boolean match = username.equals(actual.getUsername()) && "placeholderPassword".equals(actual.getPassword());
            Assertions.assertTrue(match);
        }
        else {
            Assertions.fail("getUserByUsername returned null");
        }
    }
}
