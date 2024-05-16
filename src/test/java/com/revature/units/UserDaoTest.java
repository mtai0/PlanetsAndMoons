package com.revature.units;

import com.revature.models.User;
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

    @BeforeEach
    public void setup() {
        connection = Mockito.mock(Connection.class);
        connectionUtils.when(() -> ConnectionUtil.createConnection()).thenReturn(connection);
        dao = new UserDao();
    }

    @AfterEach
    public void cleanup() {
        connection = null;
        dao = null;
    }

    @ParameterizedTest
    @DisplayName("UserDao::getUserByUsername - Valid Input")
    @Order(0)
    @CsvSource({
            "username"
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
        } catch (SQLException ignored) {}

        User actual = dao.getUserByUsername(username);
        Assertions.assertEquals(username, actual.getUsername());
    }

    @ParameterizedTest
    @DisplayName("UserDao::getUserByUsername - User Not Found")
    @Order(1)
    @CsvSource({
            "fakeUser"
    })
    public void getUserByUsernameNotFound(String username) {
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("select * from users where username = ?")).thenReturn(ps);

            ResultSet results = Mockito.mock(ResultSet.class);
            when(ps.executeQuery()).thenReturn(results);
            when(results.next()).thenReturn(false);
        } catch (SQLException ignored) {}

        User actual = dao.getUserByUsername(username);
        Assertions.assertNull(actual);
    }
}
