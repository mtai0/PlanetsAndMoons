package com.revature.units;

import com.revature.models.Planet;
import com.revature.repository.PlanetDao;
import com.revature.repository.UserDao;
import com.revature.service.PlanetService;
import com.revature.service.UserService;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;

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

    @ParameterizedTest
    @DisplayName("PlanetService::getPlanetByName - Success")
    @CsvSource({
            "1,Planet1",
            "1,Planet2",
            "2,Planet3",
            "2,Planet44"
    })
    public void getPlanetByIdSuccess(int ownerId, String planetName) {
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
    public void getPlanetByIdSuccess() {
        int ownerId = 1;
        String planetName = "PlanetDoesNotExist";

        when(dao.getPlanetByName(ownerId, planetName)).thenReturn(null);
        Planet actual = planetService.getPlanetByName(ownerId, planetName);
        Assertions.assertNull(actual);
    }

    @ParameterizedTest
    @DisplayName("PlanetService::getPlanetById - Success")
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
    public void getPlanetByIdFailure() {
        int ownerId = -1;
        int planetId = -1;
        when(dao.getPlanetById(ownerId, planetId)).thenReturn(null);
        Planet actual = planetService.getPlanetById(ownerId, planetId);
        Assertions.assertNull(actual);
    }

    @Test
    @DisplayName("PlanetService::deletePlanetById - Success")
    public void deletePlanetSuccess() {
        when(dao.deletePlanetById(1, 1)).thenReturn(true);
        Assertions.assertTrue(planetService.deletePlanetById(1, 1));
    }

    @Test
    @DisplayName("PlanetService::deletePlanetById - Failure")
    public void deletePlanetFailure() {
        when(dao.deletePlanetById(1, -1)).thenReturn(false);
        Assertions.assertFalse(planetService.deletePlanetById(1, -1));
    }
}
