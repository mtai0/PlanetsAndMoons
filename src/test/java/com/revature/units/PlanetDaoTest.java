package com.revature.units;

import com.revature.models.Planet;
import com.revature.repository.PlanetDao;
import com.revature.repository.UserDao;
import com.revature.utilities.ConnectionUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.*;
import java.util.List;

import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PlanetDaoTest {

    @Mock
    private static Connection connection;

    @Mock
    private static MockedStatic<ConnectionUtil> connectionUtils;

    private PlanetDao dao;

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
        dao = new PlanetDao();
    }

    @AfterEach
    public void cleanup() {
        connection = null;
        dao = null;
    }

    @Test
    @DisplayName("PlanetDao::getAllPlanets - Success")
    @Order(0)
    public void getAllPlanetsSuccess() {
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("select * from planets where ownerId = ?")).thenReturn(ps);

            Planet planet1 = new Planet();
            planet1.setOwnerId(1);
            planet1.setName("Planet1");
            planet1.setId(1);

            ResultSet results = Mockito.mock(ResultSet.class);
            when(ps.executeQuery()).thenReturn(results);

            //Fake resultset iteration. Unsafe.
            var ref = new Object() {
                int currentRow = 0;
            };
            when(results.getRow()).thenReturn(ref.currentRow);
            doAnswer(invocation -> {
                ref.currentRow = ref.currentRow + 1;
                return ref.currentRow <= 1;
            }).when(results).next();

            when(results.getInt(1)).thenReturn(planet1.getId());
            when(results.getString("name")).thenReturn(planet1.getName());
            when(results.getInt("ownerId")).thenReturn(planet1.getOwnerId());

            List<Planet> planetList = dao.getAllPlanets(1);
            boolean correctSize = planetList.size() == 1;
            boolean planetsMatch = false;
            if (correctSize) {
                Planet actual = planetList.get(0);
                planetsMatch = actual.getName().equals(planet1.getName())
                        && actual.getId() == planet1.getId()
                        && actual.getOwnerId() == planet1.getOwnerId();
            }
            Assertions.assertTrue(planetsMatch);
        } catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }

    @Test
    @DisplayName("PlanetDao::getAllPlanets - Failure")
    @Order(1)
    public void getAllPlanetsFailure() {
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("select * from planets where ownerId = ?")).thenReturn(ps);
            ResultSet results = Mockito.mock(ResultSet.class);
            when(ps.executeQuery()).thenReturn(results);
            when(results.next()).thenReturn(false);

            List<Planet> planetList = dao.getAllPlanets(1);
            Assertions.assertEquals(0, planetList.size());
        }
        catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }

    @ParameterizedTest
    @DisplayName("PlanetDao::getPlanetByName - Success")
    @Order(2)
    @CsvSource({
            "1,Planet1",
            "1,Planet2",
            "2,Planet3",
            "2,Planet4"
    })
    public void getPlanetByNameSuccess(int ownerId, String name) {

        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("select * from planets where name = ? and ownerId = ?")).thenReturn(ps);
            ResultSet results = Mockito.mock(ResultSet.class);
            when(ps.executeQuery()).thenReturn(results);
            when(results.next()).thenReturn(true);
            when(results.getInt("ownerId")).thenReturn(ownerId);
            when(results.getString("name")).thenReturn(name);
            when(results.getInt(1)).thenReturn(1);

            Planet actual = dao.getPlanetByName(ownerId, name);
            boolean planetMatches = actual.getName().equals(name) && actual.getId() == 1 && actual.getOwnerId() == ownerId;
            Assertions.assertTrue(planetMatches);
        }
        catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }

    @Test
    @DisplayName("PlanetDao::getPlanetByName - Failure")
    @Order(3)
    public void getPlanetByNameFailure() {

        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("select * from planets where name = ? and ownerId = ?")).thenReturn(ps);
            ResultSet results = Mockito.mock(ResultSet.class);
            when(ps.executeQuery()).thenReturn(results);
            when(results.next()).thenReturn(false);

            Planet actual = dao.getPlanetByName(1, "CannotFind");
            Assertions.assertNull(actual);
        }
        catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }

    @ParameterizedTest
    @DisplayName("PlanetDao::getPlanetById - Success")
    @Order(4)
    @CsvSource({
            "1,1",
            "1,2",
            "2,3",
            "2,4"
    })
    public void getPlanetByIdSuccess(int ownerId, int id) {

        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("select * from planets where id = ? and ownerId = ?")).thenReturn(ps);
            ResultSet results = Mockito.mock(ResultSet.class);
            when(ps.executeQuery()).thenReturn(results);
            when(results.next()).thenReturn(true);
            when(results.getInt("ownerId")).thenReturn(ownerId);
            when(results.getString("name")).thenReturn("FoundPlanet");
            when(results.getInt(1)).thenReturn(id);

            Planet actual = dao.getPlanetById(ownerId, id);
            boolean planetMatches = actual.getName().equals("FoundPlanet")
                    && actual.getId() == id
                    && actual.getOwnerId() == ownerId;
            Assertions.assertTrue(planetMatches);
        }
        catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }

    @Test
    @DisplayName("PlanetDao::getPlanetById - Failure")
    @Order(5)
    public void getPlanetByIdFailure() {

        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("select * from planets where id = ? and ownerId = ?")).thenReturn(ps);
            ResultSet results = Mockito.mock(ResultSet.class);
            when(ps.executeQuery()).thenReturn(results);
            when(results.next()).thenReturn(false);

            Planet actual = dao.getPlanetById(1, -1);
            Assertions.assertNull(actual);
        }
        catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }

    @ParameterizedTest
    @DisplayName("PlanetDao::createPlanet - Success")
    @Order(6)
    @CsvSource({
            "1,Planet1",
            "1,Planet2",
            "2,Planet3",
            "2,Planet4"
    })
    public void createPlanetSuccess(int ownerId, String planetName) {
        Planet toAdd = new Planet();
        toAdd.setOwnerId(ownerId);
        toAdd.setName(planetName);

        PreparedStatement psCheckPlanetExists = Mockito.mock(PreparedStatement.class);
        PreparedStatement psCheckMoonExists = Mockito.mock(PreparedStatement.class);
        PreparedStatement psInsert = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("select * from planets where name = ? and ownerId = ?"))
                    .thenReturn(psCheckPlanetExists);
            when(connection.prepareStatement("SELECT moons.id, moons.name, moons.myPlanetId FROM planets INNER JOIN moons ON planets.id = moons.myPlanetId WHERE planets.ownerId = ? and moons.name = ?"))
                    .thenReturn(psCheckMoonExists);
            when(connection.prepareStatement("insert into planets (name, ownerId) values (?, ?)"
                    , Statement.RETURN_GENERATED_KEYS))
                    .thenReturn(psInsert);

            ResultSet resultsCheckPlanet = Mockito.mock(ResultSet.class);
            ResultSet resultsCheckMoon = Mockito.mock(ResultSet.class);
            ResultSet resultsInsert = Mockito.mock(ResultSet.class);

            doNothing().when(psCheckPlanetExists).setString(1, planetName);
            doNothing().when(psCheckPlanetExists).setInt(2, ownerId);
            when(psCheckPlanetExists.executeQuery()).thenReturn(resultsCheckPlanet);
            when(resultsCheckPlanet.next()).thenReturn(false);

            doNothing().when(psCheckMoonExists).setInt(1, ownerId);
            doNothing().when(psCheckMoonExists).setString(2, planetName);
            when(psCheckMoonExists.executeQuery()).thenReturn(resultsCheckMoon);
            when(resultsCheckMoon.next()).thenReturn(false);

            doNothing().when(psInsert).setString(1, planetName);
            doNothing().when(psInsert).setInt(2, ownerId);
            when(psInsert.getGeneratedKeys()).thenReturn(resultsInsert);
            when(resultsInsert.getInt(1)).thenReturn(1);

            Planet actual = dao.createPlanet(toAdd);
            boolean condition = toAdd.getName().equals(actual.getName()) && toAdd.getOwnerId() == actual.getOwnerId();
            Assertions.assertTrue(condition);
        }
        catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }

    @Test
    @DisplayName("PlanetDao::createPlanet - Failure - Planet Already Exists")
    @Order(7)
    public void createPlanetAlreadyExists() {
        String planetName = "ThisPlanetAlreadyExists";
        int ownerId = 1;
        Planet toAdd = new Planet();
        toAdd.setOwnerId(ownerId);
        toAdd.setName(planetName);

        PreparedStatement psCheckPlanetExists = Mockito.mock(PreparedStatement.class);
        PreparedStatement psCheckMoonExists = Mockito.mock(PreparedStatement.class);
        PreparedStatement psInsert = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("select * from planets where name = ? and ownerId = ?"))
                    .thenReturn(psCheckPlanetExists);

            doNothing().when(psCheckPlanetExists).setString(1, planetName);
            doNothing().when(psCheckPlanetExists).setInt(2, ownerId);
            ResultSet resultsCheckPlanet = Mockito.mock(ResultSet.class);
            when(psCheckPlanetExists.executeQuery()).thenReturn(resultsCheckPlanet);
            when(resultsCheckPlanet.next()).thenReturn(true);

            Planet actual = dao.createPlanet(toAdd);
            Planet expected = new Planet();
            boolean condition = actual.getName() == null
                    && actual.getOwnerId() == expected.getOwnerId()
                    && actual.getId() == expected.getId();
            Assertions.assertTrue(condition);
        }
        catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }

    @Test
    @DisplayName("PlanetDao::createPlanet - Failure - Moon Already Exists")
    @Order(8)
    public void createPlanetAlreadyExistsMoon() {
        int ownerId = 1;
        String planetName = "ThisMoonAlreadyExists";
        Planet toAdd = new Planet();
        toAdd.setOwnerId(ownerId);
        toAdd.setName(planetName);

        PreparedStatement psCheckPlanetExists = Mockito.mock(PreparedStatement.class);
        PreparedStatement psCheckMoonExists = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("select * from planets where name = ? and ownerId = ?"))
                    .thenReturn(psCheckPlanetExists);
            when(connection.prepareStatement("SELECT moons.id, moons.name, moons.myPlanetId FROM planets INNER JOIN moons ON planets.id = moons.myPlanetId WHERE planets.ownerId = ? and moons.name = ?"))
                    .thenReturn(psCheckMoonExists);

            ResultSet resultsCheckPlanet = Mockito.mock(ResultSet.class);
            ResultSet resultsCheckMoon = Mockito.mock(ResultSet.class);

            doNothing().when(psCheckPlanetExists).setString(1, planetName);
            doNothing().when(psCheckPlanetExists).setInt(2, ownerId);
            when(psCheckPlanetExists.executeQuery()).thenReturn(resultsCheckPlanet);
            when(resultsCheckPlanet.next()).thenReturn(false);

            doNothing().when(psCheckMoonExists).setInt(1, ownerId);
            doNothing().when(psCheckMoonExists).setString(2, planetName);
            when(psCheckMoonExists.executeQuery()).thenReturn(resultsCheckMoon);
            when(resultsCheckMoon.next()).thenReturn(true);

            Planet actual = dao.createPlanet(toAdd);
            Planet expected = new Planet();
            boolean condition = actual.getName() == null
                    && actual.getOwnerId() == expected.getOwnerId()
                    && actual.getId() == expected.getId();
            Assertions.assertTrue(condition);
        }
        catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }

    @Test
    @DisplayName("PlanetDao::deletePlanetById - Success (No Moons)")
    @Order(9)
    public void deletePlanetSuccessNoMoons() {
        int ownerId = 1;
        int planetId = 1;

        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        PreparedStatement psDeleteMoons = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("delete from planets where ownerId = ? and id = ?"))
                    .thenReturn(ps);
            doNothing().when(ps).setInt(1, ownerId);
            doNothing().when(ps).setInt(2, planetId);
            when(ps.executeUpdate()).thenReturn(1);

            when(connection.prepareStatement("delete from moons where myPlanetId = ?")).thenReturn(psDeleteMoons);
            doNothing().when(psDeleteMoons).setInt(1, planetId);
            when(psDeleteMoons.executeUpdate()).thenReturn(0);

            Assertions.assertTrue(dao.deletePlanetById(ownerId, planetId));
        }
        catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }

    @Test
    @DisplayName("PlanetDao::deletePlanetById - Success (Has Moons)")
    @Order(10)
    public void deletePlanetSuccessHasMoons() {
        int ownerId = 1;
        int planetId = 1;

        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        PreparedStatement psDeleteMoons = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("delete from planets where ownerId = ? and id = ?"))
                    .thenReturn(ps);
            doNothing().when(ps).setInt(1, ownerId);
            doNothing().when(ps).setInt(2, planetId);
            when(ps.executeUpdate()).thenReturn(1);

            when(connection.prepareStatement("delete from moons where myPlanetId = ?")).thenReturn(psDeleteMoons);
            doNothing().when(psDeleteMoons).setInt(1, planetId);
            when(psDeleteMoons.executeUpdate()).thenReturn(1);

            Assertions.assertTrue(dao.deletePlanetById(ownerId, planetId));
        }
        catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }

    @Test
    @DisplayName("PlanetDao::deletePlanetById - Failure")
    @Order(11)
    public void deletePlanetFailure() {
        int ownerId = 1;
        int planetId = -1;

        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        PreparedStatement psDeleteMoons = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("delete from planets where ownerId = ? and id = ?"))
                    .thenReturn(ps);
            doNothing().when(ps).setInt(1, ownerId);
            doNothing().when(ps).setInt(2, planetId);
            when(ps.executeUpdate()).thenReturn(0);

            Assertions.assertFalse(dao.deletePlanetById(ownerId, planetId));
        }
        catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }
}
