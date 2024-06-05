package com.revature.integration.databasedao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.revature.models.Moon;
import com.revature.models.Planet;
import com.revature.repository.MoonDao;
import com.revature.repository.PlanetDao;
import com.revature.repository.UserDao;
import com.revature.utilities.ConnectionUtil;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MoonDaoIntegrationTest {
    private MoonDao dao;
    private PlanetDao pdao;
    private UserDao udao;
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
        pdao = new PlanetDao();
        
        
    }

    @ParameterizedTest
    @Order(0)
    @DisplayName("Integration::MoonDao::createMoon - Success")
    @CsvSource({
        "Luna, -1 ,t1",
        "Phobos, -1, t2",
        "Deimos, -1, t3"
    })
    public void createMoon(String name, int planetId, String planetName) {
       
    
          int userId= -1;
        String username = "user";
        String password = "pass";
        //Populate database
        try (Connection connection = ConnectionUtil.createConnection()) {

            //Add user to DB
            String sql2 = "insert into users (username, password) values (?, ?)";
            PreparedStatement ps2 = connection.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
            ps2.setString(1, username);
            ps2.setString(2, password);
            int userRowsUpdated = ps2.executeUpdate();
            if (userRowsUpdated <= 0) Assertions.fail("Failed to set up User");
            ResultSet rsUsers = ps2.getGeneratedKeys();
            userId = rsUsers.getInt(1);

            //Add Planet to DB
            String sql = "insert into planets (name, ownerId) values (?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, planetName);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch(SQLException e) {
            Assertions.fail(" failed to populate database due to a SQLException.");
        }
         Planet planet= pdao.getPlanetByName(userId, planetName);
        Moon moon = new Moon();
        moon.setName(name);
        moon.setMyPlanetId(planet.getId());

        Moon actual = dao.createMoon(userId, moon); // Assuming ownerId is 1 for simplicity
        
        Assertions.assertEquals(name, actual.getName());
    }

    @ParameterizedTest
    @Order(1)
    @DisplayName("Integration::MoonDao::getMoonByName - Success")
    @CsvSource({
        "Luna, 1, t1",
        "Phobos, 2, t2"
    })
    public void getMoonByName(String moonName, int ownerId, String planetName) {


        int userId= -1;
        String username = "user";
        String password = "pass";
        //Populate database
        try (Connection connection = ConnectionUtil.createConnection()) {

            //Add user to DB
            String sql2 = "insert into users (username, password) values (?, ?)";
            PreparedStatement ps2 = connection.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
            ps2.setString(1, username);
            ps2.setString(2, password);
            int userRowsUpdated = ps2.executeUpdate();
            if (userRowsUpdated <= 0) Assertions.fail("Failed to set up User");
            ResultSet rsUsers = ps2.getGeneratedKeys();
            userId = rsUsers.getInt(1);

            //Add Planet to DB
            String sql = "insert into planets (name, ownerId) values (?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, planetName);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch(SQLException e) {
            Assertions.fail(" failed to populate database due to a SQLException.");
        }
         Planet planet= pdao.getPlanetByName(userId, planetName);
        Moon moon = new Moon();
        moon.setName(moonName);
        moon.setMyPlanetId(planet.getId());

        Moon actual = dao.createMoon(userId, moon); // Assuming ownerId is 1 for simplicity

       

      
        Assertions.assertEquals(moonName, actual.getName());
    }


    @Test
    @Order(2)
    @DisplayName("Integration::MoonDao::getMoonById - Success")
    public void getMoonById() {

        
        int userId= -1;
        String username = "user";
        String password = "pass";
        //Populate database
        try (Connection connection = ConnectionUtil.createConnection()) {

            //Add user to DB
            String sql2 = "insert into users (username, password) values (?, ?)";
            PreparedStatement ps2 = connection.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
            ps2.setString(1, username);
            ps2.setString(2, password);
            int userRowsUpdated = ps2.executeUpdate();
            if (userRowsUpdated <= 0) Assertions.fail("Failed to set up User");
            ResultSet rsUsers = ps2.getGeneratedKeys();
            userId = rsUsers.getInt(1);

            //Add Planet to DB
            String sql = "insert into planets (name, ownerId) values (?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, "T1");
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch(SQLException e) {
            Assertions.fail(" failed to populate database due to a SQLException.");
        }
         Planet planet= pdao.getPlanetByName(userId, "T1");
        Moon moon = new Moon();
        moon.setName("Luna");
        moon.setMyPlanetId(planet.getId());

        Moon actual = dao.createMoon(userId, moon); // Assuming ownerId is 1 for simplicity

        Moon find= dao.getMoonById(userId, actual.getId());
        
        Assertions.assertEquals(actual.getId(), find.getId());
      
    }

    @Test
    @Order(3)
    @DisplayName("Integration::MoonDao::getAllMoons - Success")
    public void getAllMoons() {

        int userId= -1;
        String username = "user";
        String password = "pass";
        //Populate database
        try (Connection connection = ConnectionUtil.createConnection()) {

            //Add user to DB
            String sql2 = "insert into users (username, password) values (?, ?)";
            PreparedStatement ps2 = connection.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
            ps2.setString(1, username);
            ps2.setString(2, password);
            int userRowsUpdated = ps2.executeUpdate();
            if (userRowsUpdated <= 0) Assertions.fail("Failed to set up User");
            ResultSet rsUsers = ps2.getGeneratedKeys();
            userId = rsUsers.getInt(1);

            //Add Planet to DB
            String sql = "insert into planets (name, ownerId) values (?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, "t1");
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch(SQLException e) {
            Assertions.fail(" failed to populate database due to a SQLException.");
        }
        Planet planet= pdao.getPlanetByName(userId, "t1");
        Moon moon = new Moon();
        moon.setName("luna");
        moon.setMyPlanetId(planet.getId());

        dao.createMoon(userId, moon);
        List<Moon> moons = dao.getAllMoons(userId);
        Assertions.assertFalse(moons.isEmpty());
    }

    @Test
    @Order(4)
    @DisplayName("Integration::MoonDao::deleteMoonById - Success")
    public void deleteMoonById() {

        int userId= -1;
        String username = "user";
        String password = "pass";
        //Populate database
        try (Connection connection = ConnectionUtil.createConnection()) {

            //Add user to DB
            String sql2 = "insert into users (username, password) values (?, ?)";
            PreparedStatement ps2 = connection.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
            ps2.setString(1, username);
            ps2.setString(2, password);
            int userRowsUpdated = ps2.executeUpdate();
            if (userRowsUpdated <= 0) Assertions.fail("Failed to set up User");
            ResultSet rsUsers = ps2.getGeneratedKeys();
            userId = rsUsers.getInt(1);

            //Add Planet to DB
            String sql = "insert into planets (name, ownerId) values (?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, "t1");
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch(SQLException e) {
            Assertions.fail(" failed to populate database due to a SQLException.");
        }
        Planet planet= pdao.getPlanetByName(userId, "t1");
        Moon moon = new Moon();
        moon.setName("luna");
        moon.setMyPlanetId(planet.getId());
        dao.createMoon(userId, moon);
        Moon selected= dao.getMoonByName(userId, "luna");
        boolean result = dao.deleteMoonById(userId, selected.getId());
        Assertions.assertTrue(result);
    }

    @AfterEach
    public void cleanupTest(){
        dao = null;
        resetDatabase();
    }
}
