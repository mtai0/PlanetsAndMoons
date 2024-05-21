package com.revature.integration.databasedao;

import com.revature.repository.PlanetDao;
import com.revature.utilities.ConnectionUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PlanetDaoTest {
    private PlanetDao dao;

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
        dao = new PlanetDao();
        resetDatabase();
    }

    @AfterEach
    public void cleanupTest(){
        dao = null;
        resetDatabase();
    }
}
