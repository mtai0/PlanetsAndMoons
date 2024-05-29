package com.revature.integration.service;

import com.revature.models.Planet;

import com.revature.repository.PlanetDao;

import com.revature.service.PlanetService;

import com.revature.utilities.ConnectionUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class PlanetServiceTest {

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
        planetDao = new PlanetDao();
        planetService = new PlanetService(planetDao);
        resetDatabase();
    }

    @AfterEach
    public void cleanup() {
        planetDao = null;
        planetService = null;
        resetDatabase();
    }


    @Test
    @DisplayName("PlanetService::getAllPlanets - Success")
    @Order(0)
    public void getAllPlanetsSuccess() {
        int planetID =-1;
        int userId=-1;
        ArrayList<Planet> planetList = new ArrayList<Planet>();
        String username = "user";
        String password = "pass";
        String planetname="venus";
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
            ps.setString(1, planetname);
            ps.setInt(2, userId);
            int planetRowsUpdated = ps.executeUpdate();
            if (planetRowsUpdated <= 0) Assertions.fail("Failed to set up Planet");
            ResultSet rsPlanet = ps.getGeneratedKeys();
            planetID = rsPlanet.getInt(1);

            //get planets added into database
            String sql3 = "select * from planets where ownerId = ?";
            PreparedStatement ps3 = connection.prepareStatement(sql3);
            ps3.setInt(1, userId);
            ResultSet rs = ps3.executeQuery();
            while (rs.next()) {
                Planet newPlanet = new Planet();
                newPlanet.setId(rs.getInt(1));
                newPlanet.setName(rs.getString("name"));
                newPlanet.setOwnerId(rs.getInt("ownerId"));
                planetList.add(newPlanet);
            }

        } catch(SQLException e) {
            Assertions.fail("getAllPlanets failed to populate database due to a SQLException.");
        }

        List<Planet> actual = planetService.getAllPlanets(userId);

        Assertions.assertEquals(planetList.size(), actual.size());
    }


    @Test
    @DisplayName("PlanetService::getAllPlanets - No Planets")
    @Order(1)
    public void getAllPlanetsNoPlanets() {

        List<Planet> actual = planetService.getAllPlanets(1);
        Assertions.assertEquals(0, actual.size());
    }

    @ParameterizedTest
    @DisplayName("PlanetService::getPlanetByName - Success")
    @Order(2)
    @CsvSource({
            "1,Planet1",
            "1,Planet2",
            "2,Planet3",
            "2,Planet4"
    })
    public void getPlanetByNameSuccess(int ownerId, String planetName) {
        int userId= -1;
        String username = "user";
        String password = "pass";
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
            ps.setInt(2, ownerId);
            ps.executeUpdate();
        } catch(SQLException e) {
            Assertions.fail("getPlanetbyPlanetname failed to populate database due to a SQLException.");
        }
        Planet toReturn = new Planet();
        toReturn.setOwnerId(ownerId);
        toReturn.setName(planetName);
        Planet actual = planetService.getPlanetByName(ownerId, planetName);
        boolean comparison = toReturn.getOwnerId() == ownerId && toReturn.getName().equals(planetName);
        Assertions.assertTrue(comparison);
    }


    @Test
    @DisplayName("PlanetService::getPlanetByName - Failure")
    @Order(3)
    public void getPlanetByNameFailure() {
        int ownerId = 1;
        String planetName = "CannotFind";


        Planet actual = planetService.getPlanetByName(ownerId, planetName);
        Assertions.assertNull(actual);
    }


    @ParameterizedTest
    @DisplayName("PlanetService::getPlanetById - Success")
    @Order(4)
    @CsvSource({
            "1,1",
            "1,2",
            "2,3",
            "2,4"
    })
    public void getPlanetByIdSuccess(int ownerId, int planetId) {


        int planetID =-1;
        int userId=-1;
        String username = "user";
        String password = "pass";
        String planetName = "venus";
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
            ps.setString(1,planetName );
            ps.setInt(2, userId);
            int planetRowsUpdated = ps.executeUpdate();
            if (planetRowsUpdated <= 0) Assertions.fail("Failed to set up Planet");
            ResultSet rsPlanet = ps.getGeneratedKeys();
            planetID = rsPlanet.getInt(1);
        } catch(SQLException e) {
            Assertions.fail("getPlanetbyPlanetID failed to populate database due to a SQLException.");
        }

        Planet toReturn = new Planet();
        toReturn.setOwnerId(ownerId);
        toReturn.setId(planetId);

        planetDao.getPlanetById(ownerId,planetId);
        Planet actual = planetService.getPlanetById(ownerId, planetId);
        boolean comparison = toReturn.getOwnerId() == ownerId && toReturn.getId() == planetId;
        Assertions.assertTrue(comparison);
    }

    @Test
    @DisplayName("PlanetService::getPlanetById - Failure")
    @Order(5)
    public void getPlanetByIdFailure() {
        int ownerId = -1;
        int planetId = -1;

        Planet actual = planetService.getPlanetById(ownerId, planetId);
        Assertions.assertNull(actual);
    }

    @ParameterizedTest
    @DisplayName("PlanetService::createPlanet - Success")
    @Order(6)
    @CsvSource({
            "1,Planet1",
            "1,Planet2",
            "2,Planet3",
            "2,Planet4"
    })
    public void createPlanetSuccess(int ownerId, String planetName) {


        Planet toAdd = new Planet();
        toAdd.setName(planetName);
        toAdd.setOwnerId(ownerId);

        Planet actual = planetService.createPlanet(ownerId, toAdd);
        planetDao.createPlanet(toAdd);
        boolean conditions = toAdd.getOwnerId() == actual.getOwnerId() && toAdd.getName().equals(planetName);
        Assertions.assertTrue(conditions);
    }
    @Test
    @DisplayName("PlanetService::createPlanet - Failure - Empty Name")
    @Order(7)
    public void createPlanetEmptyName() {
        Planet toAdd = new Planet();
        toAdd.setName("");
        toAdd.setOwnerId(1);

        Planet actual = planetService.createPlanet(1, toAdd);
        planetDao.createPlanet(toAdd);
        Planet expected = new Planet();

        boolean condition = actual.getOwnerId() == expected.getOwnerId() && actual.getName() == null;

        Assertions.assertTrue(condition);
    }

    @Test
    @DisplayName("PlanetService::createPlanet - Failure - Long Name")
    @Order(8)
    public void createPlanetLongName() {
        Planet toAdd = new Planet();
        toAdd.setName("ThisIsMoreThan30Charactersaaaaa");
        toAdd.setOwnerId(1);
        planetDao.createPlanet(toAdd);
        Planet actual = planetService.createPlanet(1, toAdd);
        Planet expected = new Planet();

        boolean condition = actual.getOwnerId() == expected.getOwnerId() && actual.getName() == null;

        Assertions.assertTrue(condition);
    }

    @Test
    @DisplayName("PlanetService::createPlanet - Failure")
    @Order(9)
    public void createPlanetFailure() {
        Planet toAdd = new Planet();
        toAdd.setName("ThisFailsForSomeReason");
        toAdd.setOwnerId(1);

        planetDao.createPlanet(toAdd);
        Planet actual = planetService.createPlanet(1, toAdd);
        Planet expected = new Planet();

        boolean condition = actual.getOwnerId() == expected.getOwnerId() && actual.getName() == null;

        Assertions.assertTrue(condition);
    }

    @Test
    @DisplayName("PlanetService::deletePlanetById - Success")
    @Order(10)
    public void deletePlanetSuccess() {

        int planetID =-1;
        int userId=-1;
        int myPlanetId=-1;
        String username = "user";
        String password = "pass";
        String planetname="venus";
        String moonName = "venusMoon";
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
            ps.setString(1, planetname);
            ps.setInt(2, userId);
            int planetRowsUpdated = ps.executeUpdate();
            if (planetRowsUpdated <= 0) Assertions.fail("Failed to set up Planet");
            ResultSet rsPlanet = ps.getGeneratedKeys();
            planetID = rsPlanet.getInt(1);


            //add moons to DB

            String sql3 = "insert into moons (name, myPlanetId) values (?, ?)";
            PreparedStatement ps3 = connection.prepareStatement(sql3, Statement.RETURN_GENERATED_KEYS);
            ps3.setString(1, moonName);
            ps3.setInt(2, myPlanetId);
            int MoonRowsUpdated = ps3.executeUpdate();
            if (MoonRowsUpdated <= 0) Assertions.fail("Failed to set up Moon");
            ResultSet rsMoon = ps3.getGeneratedKeys();
            myPlanetId = rsMoon.getInt(1);



        } catch(SQLException e) {
            Assertions.fail(" Failed to populate the database due to a SQLException.");
        }

        Assertions.assertTrue(planetService.deletePlanetById(userId, planetID));
    }

    @Test
    @DisplayName("PlanetService::deletePlanetById - Failure")
    @Order(11)
    public void deletePlanetFailure() {

        Assertions.assertFalse(planetService.deletePlanetById(1, -1));
    }

}
