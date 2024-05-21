package com.revature.integration.databasedao;

import com.revature.repository.MoonDao;
import com.revature.utilities.ConnectionUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MoonDaoTest {
    private MoonDao dao;

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
        dao = new MoonDao();
        resetDatabase();
    }

    @AfterEach
    public void cleanupTest(){
        dao = null;
        resetDatabase();
    }
}
