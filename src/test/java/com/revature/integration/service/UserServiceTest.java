package com.revature.integration.service;

import com.revature.models.User;
import com.revature.repository.UserDao;
import com.revature.service.UserService;
import com.revature.utilities.ConnectionUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {
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
}
