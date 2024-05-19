package com.revature.units;

import com.revature.models.Moon;
import com.revature.repository.MoonDao;
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
public class MoonDaoTest {
    @Mock
    private static Connection connection;

    @Mock
    private static MockedStatic<ConnectionUtil> connectionUtils;

    private MoonDao dao;

    @BeforeAll
    public static void mockConnectionUtil() {
        connectionUtils = Mockito.mockStatic(ConnectionUtil.class);
    }

    @BeforeEach
    public void setup() {
        connection = Mockito.mock(Connection.class);
        connectionUtils.when(() -> ConnectionUtil.createConnection()).thenReturn(connection);
        dao = new MoonDao();
    }

    @AfterEach
    public void cleanup() {
        connection = null;
        dao = null;
    }

    @Test
    @DisplayName("MoonDao::getAllMoons - Success")
    @Order(0)
    public void getAllMoonsSuccess() {
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        try(Connection connection = ConnectionUtil.createConnection()) {
            when(connection.prepareStatement("SELECT moons.id, moons.name, moons.myPlanetId FROM planets INNER JOIN moons ON planets.id = moons.myPlanetId WHERE planets.ownerId = ?")).thenReturn(ps);

            int ownerId = 1;
            Moon m1 = new Moon();
            m1.setId(1);
            m1.setName("Moon1");
            m1.setMyPlanetId(1);

            ResultSet results = Mockito.mock(ResultSet.class);
            doNothing().when(ps).setInt(1, ownerId);
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

            when(results.getInt(1)).thenReturn(m1.getId());
            when(results.getString("name")).thenReturn(m1.getName());
            when(results.getInt("myPlanetId")).thenReturn(m1.getMyPlanetId());

            List<Moon> moonList = dao.getAllMoons(1);
            boolean correctSize = moonList.size() == 1;
            boolean moonsMatch = false;
            if (correctSize) {
                Moon actual = moonList.get(0);
                moonsMatch = actual.getName().equals(m1.getName())
                        && actual.getId() == m1.getId()
                        && actual.getMyPlanetId() == m1.getMyPlanetId();
            }
            Assertions.assertTrue(moonsMatch);
        } catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }

    @Test
    @DisplayName("MoonDao::getAllMoons - Failure")
    @Order(1)
    public void getAllMoonsFailure() {
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("SELECT moons.id, moons.name, moons.myPlanetId FROM planets INNER JOIN moons ON planets.id = moons.myPlanetId WHERE planets.ownerId = ?")).thenReturn(ps);

            int ownerId = 1;
            ResultSet results = Mockito.mock(ResultSet.class);
            when(ps.executeQuery()).thenReturn(results);
            when(results.next()).thenReturn(false);

            List<Moon> moonList = dao.getAllMoons(ownerId);
            Assertions.assertEquals(0, moonList.size());
        }
        catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }

    @ParameterizedTest
    @DisplayName("MoonDao::getMoonByName - Success")
    @Order(2)
    @CsvSource({
            "1,Moon1",
            "1,Moon2",
            "2,Moon3",
            "2,Moon4"
    })
    public void getMoonByNameSuccess(int ownerId, String name) {
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("SELECT moons.id, moons.name, moons.myPlanetId FROM planets INNER JOIN moons ON planets.id = moons.myPlanetId WHERE planets.ownerId = ? and moons.name = ?")).thenReturn(ps);

            doNothing().when(ps).setInt(1, ownerId);
            doNothing().when(ps).setString(2, name);
            ResultSet results = Mockito.mock(ResultSet.class);
            when(ps.executeQuery()).thenReturn(results);
            when(results.next()).thenReturn(true);

            when(results.getInt(1)).thenReturn(1);
            when(results.getString("name")).thenReturn(name);
            when(results.getInt("myPlanetId")).thenReturn(1);

            //Moon ID and Planet ID are unknowns, only thing that can be confirmed is that the returned name matches.
            Moon actual = dao.getMoonByName(ownerId, name);
            Assertions.assertEquals(name, actual.getName());
        }
        catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }

    @Test
    @DisplayName("MoonDao::getMoonByName - Failure")
    @Order(3)
    public void getMoonByNameFailure() {
        int ownerId = 1;
        String name = "CannotFind";

        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("SELECT moons.id, moons.name, moons.myPlanetId FROM planets INNER JOIN moons ON planets.id = moons.myPlanetId WHERE planets.ownerId = ? and moons.name = ?")).thenReturn(ps);

            doNothing().when(ps).setInt(1, ownerId);
            doNothing().when(ps).setString(2, name);
            ResultSet results = Mockito.mock(ResultSet.class);
            when(ps.executeQuery()).thenReturn(results);
            when(results.next()).thenReturn(false);

            Moon actual = dao.getMoonByName(ownerId, name);
            Assertions.assertNull(actual);
        }
        catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }

    @ParameterizedTest
    @DisplayName("MoonDao::getMoonById - Success")
    @Order(4)
    @CsvSource({
            "1,1",
            "1,2",
            "2,3",
            "2,4"
    })
    public void getMoonByIdSuccess(int ownerId, int moonId) {
        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("SELECT moons.id, moons.name, moons.myPlanetId FROM planets INNER JOIN moons ON planets.id = moons.myPlanetId WHERE planets.ownerId = ? and moons.id = ?")).thenReturn(ps);

            doNothing().when(ps).setInt(1, ownerId);
            doNothing().when(ps).setInt(2, moonId);
            ResultSet results = Mockito.mock(ResultSet.class);
            when(ps.executeQuery()).thenReturn(results);
            when(results.next()).thenReturn(true);

            when(results.getInt(1)).thenReturn(moonId);
            when(results.getString("name")).thenReturn("Placeholder");
            when(results.getInt("myPlanetId")).thenReturn(1);

            //Moon Name and Planet ID are unknowns, only thing that can be confirmed is that the returned name matches.
            Moon actual = dao.getMoonById(ownerId, moonId);
            Assertions.assertEquals(moonId, actual.getId());
        }
        catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }

    @Test
    @DisplayName("MoonDao::getMoonById - Failure")
    @Order(5)
    public void getMoonByIdFailure() {
        int ownerId = 1;
        int moonId = -1;

        PreparedStatement ps = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("SELECT moons.id, moons.name, moons.myPlanetId FROM planets INNER JOIN moons ON planets.id = moons.myPlanetId WHERE planets.ownerId = ? and moons.id = ?")).thenReturn(ps);

            doNothing().when(ps).setInt(1, ownerId);
            doNothing().when(ps).setInt(2, moonId);
            ResultSet results = Mockito.mock(ResultSet.class);
            when(ps.executeQuery()).thenReturn(results);
            when(results.next()).thenReturn(false);

            Moon actual = dao.getMoonById(ownerId, moonId);
            Assertions.assertNull(actual);
        }
        catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }

    @ParameterizedTest
    @DisplayName("MoonDao::createMoon - Success")
    @Order(6)
    @CsvSource({
            "1,1,Moon1",
            "1,1,Moon2",
            "1,2,Moon3",
            "1,2,Moon4"
    })
    public void createMoonSuccess(int ownerId, int myPlanetId, String name) {
        PreparedStatement psCheckOwnership = Mockito.mock(PreparedStatement.class);
        PreparedStatement psMoonExists = Mockito.mock(PreparedStatement.class);
        PreparedStatement psPlanetExists = Mockito.mock(PreparedStatement.class);
        PreparedStatement psInsert = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("select * from planets where id = ? and ownerId = ?")).thenReturn(psCheckOwnership);
            doNothing().when(psCheckOwnership).setInt(1, myPlanetId);
            doNothing().when(psCheckOwnership).setInt(2, ownerId);
            ResultSet resultsOwnership = Mockito.mock(ResultSet.class);
            when(psCheckOwnership.executeQuery()).thenReturn(resultsOwnership);
            when(resultsOwnership.next()).thenReturn(true);

            when(connection.prepareStatement("SELECT moons.id, moons.name, moons.myPlanetId FROM planets INNER JOIN moons ON planets.id = moons.myPlanetId WHERE planets.ownerId = ? and moons.name = ?")).thenReturn(psMoonExists);
            doNothing().when(psMoonExists).setInt(1, ownerId);
            doNothing().when(psMoonExists).setString(2, name);
            ResultSet resultsMoonExists = Mockito.mock(ResultSet.class);
            when(psMoonExists.executeQuery()).thenReturn(resultsMoonExists);
            when(resultsMoonExists.next()).thenReturn(false);

            when(connection.prepareStatement("select * from planets where name = ? and ownerId = ?")).thenReturn(psPlanetExists);
            doNothing().when(psPlanetExists).setString(1, name);
            doNothing().when(psPlanetExists).setInt(2, ownerId);
            ResultSet resultsPlanetExists = Mockito.mock(ResultSet.class);
            when(psPlanetExists.executeQuery()).thenReturn(resultsPlanetExists);
            when(resultsPlanetExists.next()).thenReturn(false);

            when(connection.prepareStatement("insert into moons (name, myPlanetId) values (?, ?)", Statement.RETURN_GENERATED_KEYS)).thenReturn(psInsert);
            doNothing().when(psInsert).setString(1, name);
            doNothing().when(psInsert).setInt(2, myPlanetId);
            when(psInsert.executeUpdate()).thenReturn(1);
            ResultSet resultsInsert = Mockito.mock(ResultSet.class);
            when(psInsert.getGeneratedKeys()).thenReturn(resultsInsert);
            when(resultsInsert.getInt(1)).thenReturn(1);

            Moon expected = new Moon();
            expected.setName(name);
            expected.setMyPlanetId(myPlanetId);

            Moon actual = dao.createMoon(ownerId, expected);
            boolean condition = name.equals(actual.getName()) && myPlanetId == actual.getMyPlanetId();
            Assertions.assertTrue(condition);
        }
        catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }

    @Test
    @DisplayName("MoonDao::createMoon - Failure - Ownership")
    @Order(7)
    public void createMoonFailureOwnership() {
        int ownerId = -1;
        int myPlanetId = 1;
        String name = "Placeholder";

        PreparedStatement psCheckOwnership = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("select * from planets where id = ? and ownerId = ?")).thenReturn(psCheckOwnership);
            doNothing().when(psCheckOwnership).setInt(1, myPlanetId);
            doNothing().when(psCheckOwnership).setInt(2, ownerId);
            ResultSet resultsOwnership = Mockito.mock(ResultSet.class);
            when(psCheckOwnership.executeQuery()).thenReturn(resultsOwnership);
            when(resultsOwnership.next()).thenReturn(false);


            Moon expected = new Moon();
            Moon actual = dao.createMoon(ownerId, expected);
            boolean condition = actual.getName() == null
                    && actual.getId() == expected.getId()
                    && actual.getMyPlanetId() == actual.getMyPlanetId();
            Assertions.assertTrue(condition);
        }
        catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }

    @Test
    @DisplayName("MoonDao::createMoon - Failure - Moon Exists")
    @Order(8)
    public void createMoonFailureMoonExists() {
        int ownerId = 1;
        int myPlanetId = 1;
        String name = "ExistingMoon";

        PreparedStatement psCheckOwnership = Mockito.mock(PreparedStatement.class);
        PreparedStatement psMoonExists = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("select * from planets where id = ? and ownerId = ?")).thenReturn(psCheckOwnership);
            doNothing().when(psCheckOwnership).setInt(1, myPlanetId);
            doNothing().when(psCheckOwnership).setInt(2, ownerId);
            ResultSet resultsOwnership = Mockito.mock(ResultSet.class);
            when(psCheckOwnership.executeQuery()).thenReturn(resultsOwnership);
            when(resultsOwnership.next()).thenReturn(true);

            when(connection.prepareStatement("SELECT moons.id, moons.name, moons.myPlanetId FROM planets INNER JOIN moons ON planets.id = moons.myPlanetId WHERE planets.ownerId = ? and moons.name = ?")).thenReturn(psMoonExists);
            doNothing().when(psMoonExists).setInt(1, ownerId);
            doNothing().when(psMoonExists).setString(2, name);
            ResultSet resultsMoonExists = Mockito.mock(ResultSet.class);
            when(psMoonExists.executeQuery()).thenReturn(resultsMoonExists);
            when(resultsMoonExists.next()).thenReturn(true);

            Moon expected = new Moon();
            Moon actual = dao.createMoon(ownerId, expected);
            boolean condition = actual.getName() == null
                    && actual.getId() == expected.getId()
                    && actual.getMyPlanetId() == actual.getMyPlanetId();
            Assertions.assertTrue(condition);
        }
        catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }

    @Test
    @DisplayName("MoonDao::createMoon - Failure - Planet Exists")
    @Order(9)
    public void createMoonFailurePlanetExists() {
        int ownerId = 1;
        int myPlanetId = 1;
        String name = "ExistingMoon";

        PreparedStatement psCheckOwnership = Mockito.mock(PreparedStatement.class);
        PreparedStatement psMoonExists = Mockito.mock(PreparedStatement.class);
        PreparedStatement psPlanetExists = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("select * from planets where id = ? and ownerId = ?")).thenReturn(psCheckOwnership);
            doNothing().when(psCheckOwnership).setInt(1, myPlanetId);
            doNothing().when(psCheckOwnership).setInt(2, ownerId);
            ResultSet resultsOwnership = Mockito.mock(ResultSet.class);
            when(psCheckOwnership.executeQuery()).thenReturn(resultsOwnership);
            when(resultsOwnership.next()).thenReturn(true);

            when(connection.prepareStatement("SELECT moons.id, moons.name, moons.myPlanetId FROM planets INNER JOIN moons ON planets.id = moons.myPlanetId WHERE planets.ownerId = ? and moons.name = ?")).thenReturn(psMoonExists);
            doNothing().when(psMoonExists).setInt(1, ownerId);
            doNothing().when(psMoonExists).setString(2, name);
            ResultSet resultsMoonExists = Mockito.mock(ResultSet.class);
            when(psMoonExists.executeQuery()).thenReturn(resultsMoonExists);
            when(resultsMoonExists.next()).thenReturn(false);

            when(connection.prepareStatement("select * from planets where name = ? and ownerId = ?")).thenReturn(psPlanetExists);
            doNothing().when(psPlanetExists).setString(1, name);
            doNothing().when(psPlanetExists).setInt(2, ownerId);
            ResultSet resultsPlanetExists = Mockito.mock(ResultSet.class);
            when(psPlanetExists.executeQuery()).thenReturn(resultsPlanetExists);
            when(resultsPlanetExists.next()).thenReturn(true);

            Moon expected = new Moon();
            Moon actual = dao.createMoon(ownerId, expected);
            boolean condition = actual.getName() == null
                    && actual.getId() == expected.getId()
                    && actual.getMyPlanetId() == actual.getMyPlanetId();
            Assertions.assertTrue(condition);
        }
        catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }

    @ParameterizedTest
    @DisplayName("MoonDao::deleteMoonById - Success")
    @Order(10)
    @CsvSource({
            "1,1",
            "1,2",
            "2,3",
            "2,4"
    })
    public void deleteMoonByIdSuccess(int ownerId, int moonId) {
        PreparedStatement psCheckOwnership = Mockito.mock(PreparedStatement.class);
        PreparedStatement psDelete = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("SELECT moons.id, moons.name, moons.myPlanetId FROM planets INNER JOIN moons ON planets.id = moons.myPlanetId WHERE planets.ownerId = ? AND moons.id = ?")).thenReturn(psCheckOwnership);
            doNothing().when(psCheckOwnership).setInt(1, ownerId);
            doNothing().when(psCheckOwnership).setInt(2,moonId);
            ResultSet resultsOwnership = Mockito.mock(ResultSet.class);
            when(psCheckOwnership.executeQuery()).thenReturn(resultsOwnership);
            when(resultsOwnership.next()).thenReturn(true);

            when(connection.prepareStatement("DELETE FROM moons WHERE id = ?")).thenReturn(psDelete);
            doNothing().when(psDelete).setInt(1, moonId);
            when(psDelete.executeUpdate()).thenReturn(1);

            Assertions.assertTrue(dao.deleteMoonById(ownerId, moonId));
        }
        catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }

    @Test
    @DisplayName("MoonDao::deleteMoonById - Failure - Ownership")
    @Order(11)
    public void deleteMoonByIdFailureOwnership() {
        int ownerId = -1;
        int moonId = 1;

        PreparedStatement psCheckOwnership = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("SELECT moons.id, moons.name, moons.myPlanetId FROM planets INNER JOIN moons ON planets.id = moons.myPlanetId WHERE planets.ownerId = ? AND moons.id = ?")).thenReturn(psCheckOwnership);
            doNothing().when(psCheckOwnership).setInt(1, ownerId);
            doNothing().when(psCheckOwnership).setInt(2,moonId);
            ResultSet resultsOwnership = Mockito.mock(ResultSet.class);
            when(psCheckOwnership.executeQuery()).thenReturn(resultsOwnership);
            when(resultsOwnership.next()).thenReturn(false);

            Assertions.assertFalse(dao.deleteMoonById(ownerId, moonId));
        }
        catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }

    @Test
    @DisplayName("MoonDao::deleteMoonById - Failure")
    @Order(12)
    public void deleteMoonByIdFailure() {
        int ownerId = 1;
        int moonId = -1;

        PreparedStatement psCheckOwnership = Mockito.mock(PreparedStatement.class);
        PreparedStatement psDelete = Mockito.mock(PreparedStatement.class);
        try {
            when(connection.prepareStatement("SELECT moons.id, moons.name, moons.myPlanetId FROM planets INNER JOIN moons ON planets.id = moons.myPlanetId WHERE planets.ownerId = ? AND moons.id = ?")).thenReturn(psCheckOwnership);
            doNothing().when(psCheckOwnership).setInt(1, ownerId);
            doNothing().when(psCheckOwnership).setInt(2,moonId);
            ResultSet resultsOwnership = Mockito.mock(ResultSet.class);
            when(psCheckOwnership.executeQuery()).thenReturn(resultsOwnership);
            when(resultsOwnership.next()).thenReturn(false);

            when(connection.prepareStatement("DELETE FROM moons WHERE id = ?")).thenReturn(psDelete);
            doNothing().when(psDelete).setInt(1, moonId);
            when(psDelete.executeUpdate()).thenReturn(0);

            Assertions.assertFalse(dao.deleteMoonById(ownerId, moonId));
        }
        catch (SQLException e){
            Assertions.fail("SQLException thrown.");
        }
    }
}
