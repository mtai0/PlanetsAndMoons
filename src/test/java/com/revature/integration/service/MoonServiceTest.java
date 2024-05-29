package com.revature.integration.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.revature.models.Moon;
import com.revature.repository.MoonDao;
import com.revature.repository.PlanetDao;
import com.revature.service.MoonService;
import com.revature.service.PlanetService;
import com.revature.utilities.ConnectionUtil;

public class MoonServiceTest {

    private MoonDao moonDao;
    private MoonService moonService;
     private PlanetDao planetDao;
    private PlanetService planetService;

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
        moonDao = new MoonDao();
        moonService = new MoonService(moonDao);
        planetDao = new PlanetDao();
        planetService = new PlanetService(planetDao);
        resetDatabase();
    }

    @AfterEach
    public void cleanup() {
        moonDao = null;
        moonService = null;
        resetDatabase();
    }

@Test
@DisplayName("MoonService::getAllMoons - Success")
@Order(1)
public void getAllMoonsSuccess() {
    int userId = -1;
    int planetID =-1;
    String username = "user";
    String password = "pass";
    String planetname="venus";
    ArrayList<Moon> expectedMoons = new ArrayList<>();
    
    try (Connection connection = ConnectionUtil.createConnection()) {
        // Add user to DB
        String userSql = "INSERT INTO users (username, password) VALUES (?, ?)";
        PreparedStatement userPs = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
        userPs.setString(1, username);
        userPs.setString(2, password);
        int userRowsUpdated = userPs.executeUpdate();
        if (userRowsUpdated <= 0) Assertions.fail("Failed to set up User");
        ResultSet rsUser = userPs.getGeneratedKeys();
        if (rsUser.next()) {
            userId = rsUser.getInt(1);
        }

        // Add planets to DB
        String sql = "insert into planets (name, ownerId) values (?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, planetname);
        ps.setInt(2, userId);
        int planetRowsUpdated = ps.executeUpdate();
        if (planetRowsUpdated <= 0) Assertions.fail("Failed to set up Planet");
        ResultSet rsPlanet = ps.getGeneratedKeys();
        planetID = rsPlanet.getInt(1);
        // Add moons to DB
        String[] moonNames = {"Luna", "Phobos", "Deimos"};
        for (String moonName : moonNames) {
            String moonSql = "INSERT INTO moons (name, myPlanetId) VALUES (?, ?)";
            PreparedStatement moonPs = connection.prepareStatement(moonSql, Statement.RETURN_GENERATED_KEYS);
            moonPs.setString(1, moonName);
            moonPs.setInt(2, userId); // Assuming myPlanetId links to userId for simplicity
            int moonRowsUpdated = moonPs.executeUpdate();
            if (moonRowsUpdated <= 0) Assertions.fail("Failed to set up Moon " + moonName);
            ResultSet rsMoon = moonPs.getGeneratedKeys();
            if (rsMoon.next()) {
                Moon moon = new Moon();
                moon.setId(rsMoon.getInt(1));
                moon.setName(moonName);
                moon.setMyPlanetId(planetID);
                moonService.createMoon(userId, moon);
                expectedMoons.add(moon);
            }
        }

    } catch (SQLException e) {
        Assertions.fail("Setup failed due to SQLException: " + e.getMessage());
    }

    // Act: Retrieve moons from the service
    List<Moon> actualMoons = moonService.getAllMoons(userId);

    // Assert: Check if the retrieved moons match the expected list
 
    Assertions.assertEquals(expectedMoons.size(), actualMoons.size(), "The number of moons retrieved should match the number added");
   
}


@Test
@DisplayName("MoonService::getMoonByName - Success")
@Order(2)
public void getMoonByNameSuccess() {
    int userId = -1;
    int planetID = -1;
    String username = "user";
    String password = "pass";
    String planetName = "Venus";
    String moonName = "Phobos";  // The moon we want to test retrieval by name
    Moon expectedMoon = new Moon();

    try (Connection connection = ConnectionUtil.createConnection()) {
        // Add user to DB
        String userSql = "INSERT INTO users (username, password) VALUES (?, ?)";
        PreparedStatement userPs = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
        userPs.setString(1, username);
        userPs.setString(2, password);
        int userRowsUpdated = userPs.executeUpdate();
        if (userRowsUpdated <= 0) Assertions.fail("Failed to set up User");
        ResultSet rsUser = userPs.getGeneratedKeys();
        if (rsUser.next()) {
            userId = rsUser.getInt(1);
        }

        // Add planet to DB
        String planetSql = "INSERT INTO planets (name, ownerId) VALUES (?, ?)";
        PreparedStatement planetPs = connection.prepareStatement(planetSql, Statement.RETURN_GENERATED_KEYS);
        planetPs.setString(1, planetName);
        planetPs.setInt(2, userId);
        int planetRowsUpdated = planetPs.executeUpdate();
        if (planetRowsUpdated <= 0) Assertions.fail("Failed to set up Planet");
        ResultSet rsPlanet = planetPs.getGeneratedKeys();
        if (rsPlanet.next()) {
            planetID = rsPlanet.getInt(1);
        }

        // Add moon to DB
        String moonSql = "INSERT INTO moons (name, myPlanetId) VALUES (?, ?)";
        PreparedStatement moonPs = connection.prepareStatement(moonSql, Statement.RETURN_GENERATED_KEYS);
        moonPs.setString(1, moonName);
        moonPs.setInt(2, planetID);
        int moonRowsUpdated = moonPs.executeUpdate();
        if (moonRowsUpdated <= 0) Assertions.fail("Failed to set up Moon");
        ResultSet rsMoon = moonPs.getGeneratedKeys();
        if (rsMoon.next()) {
            expectedMoon.setId(rsMoon.getInt(1));
            expectedMoon.setName(moonName);
            expectedMoon.setMyPlanetId(planetID);
        }

    } catch (SQLException e) {
        Assertions.fail("Setup failed due to SQLException: " + e.getMessage());
    }

    // Act: Retrieve moon by name from the service
    Moon actualMoon = moonService.getMoonByName(userId, moonName);

    // Assert: Check if the retrieved moon matches the expected moon
  
    Assertions.assertEquals(expectedMoon.getName(), actualMoon.getName(), "The names of the moons should match");
 
}


@Test
@DisplayName("MoonService::getMoonById - Success")
@Order(3)
public void getMoonByIdSuccess() {
    int userId = -1;
    int planetID = -1;
    String username = "user";
    String password = "pass";
    String planetName = "Venus";
    String moonName = "Deimos";  // The moon we want to test retrieval by ID
    Moon expectedMoon = new Moon();

    try (Connection connection = ConnectionUtil.createConnection()) {
        // Add user to DB
        String userSql = "INSERT INTO users (username, password) VALUES (?, ?)";
        PreparedStatement userPs = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
        userPs.setString(1, username);
        userPs.setString(2, password);
        int userRowsUpdated = userPs.executeUpdate();
        if (userRowsUpdated <= 0) Assertions.fail("Failed to set up User");
        ResultSet rsUser = userPs.getGeneratedKeys();
        if (rsUser.next()) {
            userId = rsUser.getInt(1);
        }

        // Add planet to DB
        String planetSql = "INSERT INTO planets (name, ownerId) VALUES (?, ?)";
        PreparedStatement planetPs = connection.prepareStatement(planetSql, Statement.RETURN_GENERATED_KEYS);
        planetPs.setString(1, planetName);
        planetPs.setInt(2, userId);
        int planetRowsUpdated = planetPs.executeUpdate();
        if (planetRowsUpdated <= 0) Assertions.fail("Failed to set up Planet");
        ResultSet rsPlanet = planetPs.getGeneratedKeys();
        if (rsPlanet.next()) {
            planetID = rsPlanet.getInt(1);
        }

        // Add moon to DB
        String moonSql = "INSERT INTO moons (name, myPlanetId) VALUES (?, ?)";
        PreparedStatement moonPs = connection.prepareStatement(moonSql, Statement.RETURN_GENERATED_KEYS);
        moonPs.setString(1, moonName);
        moonPs.setInt(2, planetID);
        int moonRowsUpdated = moonPs.executeUpdate();
        if (moonRowsUpdated <= 0) Assertions.fail("Failed to set up Moon");
        ResultSet rsMoon = moonPs.getGeneratedKeys();
        if (rsMoon.next()) {
            expectedMoon.setId(rsMoon.getInt(1));
            expectedMoon.setName(moonName);
            expectedMoon.setMyPlanetId(planetID);
        }

    } catch (SQLException e) {
        Assertions.fail("Setup failed due to SQLException: " + e.getMessage());
    }

    // Act: Retrieve moon by ID from the service
    Moon actualMoon = moonService.getMoonById(userId, expectedMoon.getId());

    // Assert: Check if the retrieved moon matches the expected moon
  
    Assertions.assertEquals(expectedMoon.getId(), actualMoon.getId(), "The IDs of the moons should match");
 

}


@Test
@DisplayName("MoonService::createMoon - Success")
@Order(4)
public void createMoonSuccess() {
    int userId = -1;
    int planetID = -1;
    String username = "user";
    String password = "pass";
    String planetName = "Venus";
    String moonName = "Ganymede";  // The moon we want to create
    Moon moonToCreate = new Moon();

    try (Connection connection = ConnectionUtil.createConnection()) {
        // Add user to DB
        String userSql = "INSERT INTO users (username, password) VALUES (?, ?)";
        PreparedStatement userPs = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
        userPs.setString(1, username);
        userPs.setString(2, password);
        int userRowsUpdated = userPs.executeUpdate();
        if (userRowsUpdated <= 0) Assertions.fail("Failed to set up User");
        ResultSet rsUser = userPs.getGeneratedKeys();
        if (rsUser.next()) {
            userId = rsUser.getInt(1);
        }

        // Add planet to DB
        String planetSql = "INSERT INTO planets (name, ownerId) VALUES (?, ?)";
        PreparedStatement planetPs = connection.prepareStatement(planetSql, Statement.RETURN_GENERATED_KEYS);
        planetPs.setString(1, planetName);
        planetPs.setInt(2, userId);
        int planetRowsUpdated = planetPs.executeUpdate();
        if (planetRowsUpdated <= 0) Assertions.fail("Failed to set up Planet");
        ResultSet rsPlanet = planetPs.getGeneratedKeys();
        if (rsPlanet.next()) {
            planetID = rsPlanet.getInt(1);
        }

        // Prepare moon to be created
        moonToCreate.setName(moonName);
        moonToCreate.setMyPlanetId(planetID);

    } catch (SQLException e) {
        Assertions.fail("Setup failed due to SQLException: " + e.getMessage());
    }

    // Act: Attempt to create the moon using the service
    Moon createdMoon = moonService.createMoon(userId, moonToCreate);

    // Assert: Verify the moon creation was successful
   

    Assertions.assertEquals(moonName, createdMoon.getName(), "The names of the moon should match");
  
}


@Test
@DisplayName("MoonService::deleteMoonById - Success")
@Order(5)
public void deleteMoonByIdSuccess() {
    int userId = -1;
    int planetID = -1;
    int moonId = -1;
    String username = "user";
    String password = "pass";
    String planetName = "Venus";
    String moonName = "Callisto";  // The moon we want to delete

    try (Connection connection = ConnectionUtil.createConnection()) {
        // Add user to DB
        String userSql = "INSERT INTO users (username, password) VALUES (?, ?)";
        PreparedStatement userPs = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
        userPs.setString(1, username);
        userPs.setString(2, password);
        int userRowsUpdated = userPs.executeUpdate();
        if (userRowsUpdated <= 0) Assertions.fail("Failed to set up User");
        ResultSet rsUser = userPs.getGeneratedKeys();
        if (rsUser.next()) {
            userId = rsUser.getInt(1);
        }

        // Add planet to DB
        String planetSql = "INSERT INTO planets (name, ownerId) VALUES (?, ?)";
        PreparedStatement planetPs = connection.prepareStatement(planetSql, Statement.RETURN_GENERATED_KEYS);
        planetPs.setString(1, planetName);
        planetPs.setInt(2, userId);
        int planetRowsUpdated = planetPs.executeUpdate();
        if (planetRowsUpdated <= 0) Assertions.fail("Failed to set up Planet");
        ResultSet rsPlanet = planetPs.getGeneratedKeys();
        if (rsPlanet.next()) {
            planetID = rsPlanet.getInt(1);
        }

        // Add moon to DB
        String moonSql = "INSERT INTO moons (name, myPlanetId) VALUES (?, ?)";
        PreparedStatement moonPs = connection.prepareStatement(moonSql, Statement.RETURN_GENERATED_KEYS);
        moonPs.setString(1, moonName);
        moonPs.setInt(2, planetID);
        int moonRowsUpdated = moonPs.executeUpdate();
        if (moonRowsUpdated <= 0) Assertions.fail("Failed to set up Moon");
        ResultSet rsMoon = moonPs.getGeneratedKeys();
        if (rsMoon.next()) {
            moonId = rsMoon.getInt(1);
        }

    } catch (SQLException e) {
        Assertions.fail("Setup failed due to SQLException: " + e.getMessage());
    }

    // Act: Delete the moon using the service
    boolean deleteResult = moonService.deleteMoonById(userId, moonId);

    // Assert: Verify the moon deletion was successful
    Assertions.assertTrue(deleteResult, "The moon should be successfully deleted");

    // Verify that the moon is no longer in the database
    try (Connection connection = ConnectionUtil.createConnection()) {
        String verifySql = "SELECT * FROM moons WHERE id = ?";
        PreparedStatement verifyPs = connection.prepareStatement(verifySql);
        verifyPs.setInt(1, moonId);
        ResultSet rs = verifyPs.executeQuery();
        Assertions.assertFalse(rs.next(), "No moon should be found with the specified ID after deletion");
    } catch (SQLException e) {
        Assertions.fail("Verification failed due to SQLException: " + e.getMessage());
    }
}


@Test
@DisplayName("MoonService::getMoonsFromPlanet - Success")
@Order(6)
public void getMoonsFromPlanetSuccess() {
    int userId = -1;
    int planetID = -1;
    String username = "user";
    String password = "pass";
    String planetName = "Mars";
    ArrayList<Moon> expectedMoons = new ArrayList<>();

    try (Connection connection = ConnectionUtil.createConnection()) {
        // Add user to DB
        String userSql = "INSERT INTO users (username, password) VALUES (?, ?)";
        PreparedStatement userPs = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
        userPs.setString(1, username);
        userPs.setString(2, password);
        int userRowsUpdated = userPs.executeUpdate();
        if (userRowsUpdated <= 0) Assertions.fail("Failed to set up User");
        ResultSet rsUser = userPs.getGeneratedKeys();
        if (rsUser.next()) {
            userId = rsUser.getInt(1);
        }

        // Add planet to DB
        String planetSql = "INSERT INTO planets (name, ownerId) VALUES (?, ?)";
        PreparedStatement planetPs = connection.prepareStatement(planetSql, Statement.RETURN_GENERATED_KEYS);
        planetPs.setString(1, planetName);
        planetPs.setInt(2, userId);
        int planetRowsUpdated = planetPs.executeUpdate();
        if (planetRowsUpdated <= 0) Assertions.fail("Failed to set up Planet");
        ResultSet rsPlanet = planetPs.getGeneratedKeys();
        if (rsPlanet.next()) {
            planetID = rsPlanet.getInt(1);
        }

        // Add moons to this planet
        String[] moonNames = {"Phobos", "Deimos"};
        for (String moonName : moonNames) {
            String moonSql = "INSERT INTO moons (name, myPlanetId) VALUES (?, ?)";
            PreparedStatement moonPs = connection.prepareStatement(moonSql, Statement.RETURN_GENERATED_KEYS);
            moonPs.setString(1, moonName);
            moonPs.setInt(2, planetID);
            int moonRowsUpdated = moonPs.executeUpdate();
            if (moonRowsUpdated <= 0) Assertions.fail("Failed to set up Moon " + moonName);
            ResultSet rsMoon = moonPs.getGeneratedKeys();
            if (rsMoon.next()) {
                Moon moon = new Moon();
                moon.setId(rsMoon.getInt(1));
                moon.setName(moonName);
                moon.setMyPlanetId(planetID);
                moonService.createMoon(userId, moon);
                expectedMoons.add(moon);
            }
        }

    } catch (SQLException e) {
        Assertions.fail("Setup failed due to SQLException: " + e.getMessage());
    }

    // Act: Retrieve moons from the service by planet ID
    List<Moon> actualMoons = moonService.getMoonsFromPlanet(userId, planetID);

    // Assert: Verify that the retrieved moons match the expected list

    Assertions.assertEquals(expectedMoons.size(), actualMoons.size(), "The number of moons retrieved should match the number added");
  
}

}
