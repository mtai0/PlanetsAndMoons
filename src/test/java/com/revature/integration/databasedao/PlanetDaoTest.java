package com.revature.integration.databasedao;

import com.revature.models.Planet;
import com.revature.models.User;
import com.revature.models.UsernamePasswordAuthentication;
import com.revature.repository.PlanetDao;
import com.revature.utilities.ConnectionUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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


    @ParameterizedTest
    @DisplayName("Integration::PlanetDao-Database::creatPlanet - Success")
    @Order(0)
    @CsvSource({
            "earth",
            "venus",
            "mars"

    })
    public void createPlanet(String name){
        Planet planet = new Planet();
        planet.setName(name);
        //planet.setOwnerId(ownerId);

        Planet actual = dao.createPlanet(planet);

        if (actual != null){
            //Verify planetname matches.
            boolean match = planet.equals(actual.getName());
            Assertions.assertEquals(planet.getName(), actual.getName());
        }
        else {
            Assertions.fail("createPlanet returned null");
        }
    }



    @ParameterizedTest
    @DisplayName("Integration::PlanetDAO-Database::getPlanetbyPlanetName - Success")
    @Order(1)
    @CsvSource({
            "venus",
            "mars",
            "earth"
    })
    public void getUserByUsernameSuccess(String planetname) {
        int ownerID =1 ;
        int userId=-1;
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
            ps.setString(1, planetname);
            ps.setInt(2, ownerID);
            ps.executeUpdate();
        } catch(SQLException e) {
            Assertions.fail("getPlanetbyPlanetname failed to populate database due to a SQLException.");
        }

        Planet actual = dao.getPlanetByName(ownerID,planetname);
        if (actual != null){
            Assertions.assertEquals(planetname, actual.getName());
        }
        else {
            Assertions.fail("getPlanetbyPlanetName returned null");
        }
    }

    @ParameterizedTest
    @DisplayName("Integration::PlanetDAO-Database::getPlanetbyPlanetName - Success")
    @Order(2)
    @CsvSource({
            "venus",
            "mars",
            "earth"
    })
    public void getPlanetById(String planetname) {
        int ownerID =-1 ;
        int planetID =-1;
        int userId=-1;
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
            ps.setString(1, planetname);
            ps.setInt(2, ownerID);
            int planetRowsUpdated = ps.executeUpdate();
            if (planetRowsUpdated <= 0) Assertions.fail("Failed to set up Planet");
            ResultSet rsPlanet = ps.getGeneratedKeys();
            planetID = rsPlanet.getInt(1);
        } catch(SQLException e) {
            Assertions.fail("getPlanetbyPlanetID failed to populate database due to a SQLException.");
        }

        Planet actual = dao.getPlanetById(ownerID,planetID);
        if (actual != null){
            Assertions.assertEquals(planetID, actual.getId());
        }
        else {
            Assertions.fail("getPlanetbyPlanetID returned null");
        }
    }


    @Test
    @DisplayName("Integration::PlanetDAO-Database::getAllPlanets - Success")
    @Order(3)
    public void getAllPlanets() {
        int ownerID =-1 ;
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
            ps.setInt(2, ownerID);
            int planetRowsUpdated = ps.executeUpdate();
            if (planetRowsUpdated <= 0) Assertions.fail("Failed to set up Planet");
            ResultSet rsPlanet = ps.getGeneratedKeys();
            planetID = rsPlanet.getInt(1);

            //get planets added into database
            String sql3 = "select * from planets where ownerId = ?";
            PreparedStatement ps3 = connection.prepareStatement(sql3);
            ps3.setInt(1, ownerID);
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

        List<Planet> actual = dao.getAllPlanets(ownerID);
        if (actual != null){
            Assertions.assertEquals(planetList.size(), actual.size());
        }
        else {
            Assertions.fail("getAllPlanets returned null");
        }
    }



    @Test
    @DisplayName("Integration::PlanetDAO-Database::deletePlanetbyPlanetID - Success")
    @Order(4)
    public void deletePLanetByID() {
        int ownerID =-1 ;
        int planetID =-1;
        int userId=-1;
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
            ps.setInt(2, ownerID);
            int planetRowsUpdated = ps.executeUpdate();
            if (planetRowsUpdated <= 0) Assertions.fail("Failed to set up Planet");
            ResultSet rsPlanet = ps.getGeneratedKeys();
            planetID = rsPlanet.getInt(1);


        } catch(SQLException e) {
            Assertions.fail("deletePlanetsbyID failed to delete from database due to a SQLException.");
        }

        Planet actual = dao.getPlanetById(ownerID,planetID);
        if (actual != null){
            Assertions.assertFalse(actual.getId()==-1, "UserID correct");
            Assertions.assertFalse(actual.getId()!=-1, "PlanetID correct");
            Assertions.assertFalse(userId==-1 && actual.getId()==-1);
        }
        else {
            Assertions.fail("deletePlanetbyID returned null");
        }
    }


    @AfterEach
    public void cleanupTest(){
        dao = null;
        resetDatabase();
    }
}
