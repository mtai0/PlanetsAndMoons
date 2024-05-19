package com.revature.units;

import com.revature.models.User;
import com.revature.models.UsernamePasswordAuthentication;
import com.revature.repository.UserDao;
import com.revature.utilities.ConnectionUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDaoTest {
    @Mock
    private static Connection connection;

    @Mock
    private static MockedStatic<ConnectionUtil> connectionUtils;

    private UserDao dao;

    @BeforeAll
    public static void mockConnectionUtil() {
        connectionUtils = Mockito.mockStatic(ConnectionUtil.class);
    }

    @AfterAll
    public static void unmockConnectionUtil() {
        connectionUtils.close();
    }

    @BeforeEach
    public void setup() {
        connection = Mockito.mock(Connection.class);
        connectionUtils.when(ConnectionUtil::createConnection).thenReturn(connection);
        dao = new UserDao();
    }

    @AfterEach
    public void cleanup() {
        connection = null;
        dao = null;
    }

    @ParameterizedTest
    @DisplayName("UserDao::getUserByUsername - Success")
    @Order(0)
    @CsvSource({
            "username",
            "test",
            "user2"
    })
    public void getUserByUsername(String username) {
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("select * from users where username = ?")).thenReturn(ps);

            ResultSet results = Mockito.mock(ResultSet.class);
            when(ps.executeQuery()).thenReturn(results);
            when(results.next()).thenReturn(true);
            when(results.getInt("id")).thenReturn(0);
            when(results.getString("username")).thenReturn(username);
            when(results.getString("password")).thenReturn("fillerPassword");
        } catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }

        User actual = dao.getUserByUsername(username);
        Assertions.assertEquals(username, actual.getUsername());
    }

    @Test
    @DisplayName("UserDao::getUserByUsername - Failure - User Not Found")
    @Order(1)
    public void getUserByUsernameNotFound() {
        String username = "UserDoesNotExist";
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("select * from users where username = ?")).thenReturn(ps);

            ResultSet results = Mockito.mock(ResultSet.class);
            when(ps.executeQuery()).thenReturn(results);
            when(results.next()).thenReturn(false);
        } catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }

        User actual = dao.getUserByUsername(username);
        Assertions.assertNull(actual);
    }

    @ParameterizedTest
    @DisplayName("UserDao::createUser - Success")
    @Order(2)
    @CsvSource({
            "username,password",
            "test,testpassword",
            "user3,12345"
    })
    public void createUserSuccess(String username, String password) {
        UsernamePasswordAuthentication auth = new UsernamePasswordAuthentication();
        auth.setUsername(username);
        auth.setPassword(password);

        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement(
                    "insert into users (username, password) values (?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)
            ).thenReturn(ps);
            doNothing().when(ps).setString(1, username);
            doNothing().when(ps).setString(2, password);

            when(ps.executeUpdate()).thenReturn(1);
            ResultSet results = Mockito.mock(ResultSet.class);

            when(ps.getGeneratedKeys()).thenReturn(results);

            when(results.first()).thenReturn(true);
            when(results.getInt(1)).thenReturn(0);

            User actual = dao.createUser(auth);
            boolean credentialsMatch = actual.getUsername().equals(username) && actual.getPassword().equals(password);
            Assertions.assertTrue(credentialsMatch);
        } catch (SQLException ignored) {}
    }

    @Test
    @DisplayName("UserDao::createUser - Failure")
    @Order(3)
    public void createUserFailure() {
        String username = "test";
        String password = "test";
        UsernamePasswordAuthentication auth = new UsernamePasswordAuthentication();
        auth.setUsername(username);
        auth.setPassword(password);

        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement(
                    "insert into users (username, password) values (?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)
            ).thenReturn(ps);
            doNothing().when(ps).setString(1, username);
            doNothing().when(ps).setString(2, password);

            when(ps.executeUpdate()).thenReturn(1);
            ResultSet results = Mockito.mock(ResultSet.class);

            when(ps.getGeneratedKeys()).thenReturn(results);

            when(results.first()).thenReturn(false);

            User expected = new User();
            User actual = dao.createUser(auth);
            boolean condition = actual.getUsername() == null && actual.getPassword() == null && actual.getId() == expected.getId();
            Assertions.assertTrue(condition);
        } catch (SQLException ignored) {}
    }
}
