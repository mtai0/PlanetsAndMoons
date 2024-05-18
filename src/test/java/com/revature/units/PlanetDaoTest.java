package com.revature.units;

import com.revature.models.Planet;
import com.revature.repository.PlanetDao;
import com.revature.repository.UserDao;
import com.revature.utilities.ConnectionUtil;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    public void setup() {
        connection = Mockito.mock(Connection.class);
        connectionUtils.when(() -> ConnectionUtil.createConnection()).thenReturn(connection);
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

            //Fake resultset iteration.
            var ref = new Object() {
                int currentRow = 0;
            };
            when(results.getRow()).thenReturn(ref.currentRow);
            doAnswer(invocation -> {
                ref.currentRow = ref.currentRow + 1;
                return ref.currentRow <= 1;
            }).when(results).next();

            doAnswer(invocationOnMock -> planet1.getId()).when(results).getInt(1);
            doAnswer(invocationOnMock -> planet1.getName()).when(results).getString("name");
            doAnswer(invocationOnMock -> planet1.getOwnerId()).when(results).getInt("ownerId");

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
            e.printStackTrace();
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
            e.printStackTrace();
            Assertions.fail("SQLException thrown.");
        }
    }
}
