package com.revature.units;

import com.revature.models.Planet;
import com.revature.repository.PlanetDao;
import com.revature.service.PlanetService;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PlanetServiceTest {
    @Mock
    private PlanetDao dao;

    private PlanetService planetService;

    @BeforeEach
    public void setup() {
        dao = Mockito.mock(PlanetDao.class);
        planetService = new PlanetService(dao);
    }

    @AfterEach
    public void cleanup() {
        dao = null;
        planetService = null;
    }

    @Test
    @DisplayName("PlanetService::getAllPlanets - Success")
    @Order(0)
    public void getAllPlanetsSuccess() {
        ArrayList<Planet> input = new ArrayList<Planet>();
        Planet p1 = new Planet();
        p1.setName("Planet 1");
        p1.setOwnerId(1);
        p1.setId(1);
        input.add(p1);

        Planet p2 = new Planet();
        p2.setName("Planet 2");
        p2.setOwnerId(1);
        p2.setId(2);
        input.add(p2);

        when(dao.getAllPlanets(1)).thenReturn(input);

        List<Planet> actual = planetService.getAllPlanets(1);

        Assertions.assertEquals(input, actual);
    }

    @Test
    @DisplayName("PlanetService::getAllPlanets - No Planets")
    @Order(1)
    public void getAllPlanetsNoPlanets() {
        when(dao.getAllPlanets(1)).thenReturn(new ArrayList<Planet>());
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
        Planet toReturn = new Planet();
        toReturn.setOwnerId(ownerId);
        toReturn.setName(planetName);

        when(dao.getPlanetByName(ownerId, planetName)).thenReturn(toReturn);
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

        when(dao.getPlanetByName(ownerId, planetName)).thenReturn(null);
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
        Planet toReturn = new Planet();
        toReturn.setOwnerId(ownerId);
        toReturn.setId(planetId);

        when(dao.getPlanetById(ownerId, planetId)).thenReturn(toReturn);
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
        when(dao.getPlanetById(ownerId, planetId)).thenReturn(null);
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

        when(dao.createPlanet(toAdd)).thenReturn(toAdd);
        Planet actual = planetService.createPlanet(ownerId, toAdd);
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

        when(dao.createPlanet(toAdd)).thenReturn(new Planet());
        Planet actual = planetService.createPlanet(1, toAdd);
        Planet expected = new Planet();

        boolean condition = actual.getOwnerId() == expected.getOwnerId() && actual.getName() == null;

        Assertions.assertTrue(condition);
    }

    @Test
    @DisplayName("PlanetService::deletePlanetById - Success")
    @Order(10)
    public void deletePlanetSuccess() {
        when(dao.deletePlanetById(1, 1)).thenReturn(true);
        Assertions.assertTrue(planetService.deletePlanetById(1, 1));
    }

    @Test
    @DisplayName("PlanetService::deletePlanetById - Failure")
    @Order(11)
    public void deletePlanetFailure() {
        when(dao.deletePlanetById(1, -1)).thenReturn(false);
        Assertions.assertFalse(planetService.deletePlanetById(1, -1));
    }
}
