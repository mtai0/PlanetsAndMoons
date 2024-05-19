package com.revature.units;

import com.revature.models.Moon;
import com.revature.models.Planet;
import com.revature.repository.MoonDao;
import com.revature.service.MoonService;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MoonServiceTest {
    @Mock
    private MoonDao dao;

    private MoonService moonService;

    @BeforeEach
    public void setup() {
        dao = Mockito.mock(MoonDao.class);
        moonService = new MoonService(dao);
    }

    @AfterEach
    public void cleanup() {
        dao = null;
        moonService = null;
    }

    @Test
    @DisplayName("MoonService::getAllMoons - Success")
    @Order(0)
    public void getAllMoonsSuccess() {
        ArrayList<Moon> input = new ArrayList<Moon>();

        Moon m1 = new Moon();
        m1.setName("Moon 1");
        m1.setMyPlanetId(1);
        m1.setId(1);
        input.add(m1);

        Moon m2 = new Moon();
        m2.setName("Moon 2");
        m2.setMyPlanetId(1);
        m2.setId(2);
        input.add(m2);

        when(dao.getAllMoons(1)).thenReturn(input);

        List<Moon> actual = moonService.getAllMoons(1);
        Assertions.assertEquals(input, actual);
    }

    @Test
    @DisplayName("MoonService::getAllMoons - Failure")
    @Order(1)
    public void getAllMoonsFailure() {

        when(dao.getAllMoons(1)).thenReturn(new ArrayList<Moon>());

        List<Moon> actual = moonService.getAllMoons(1);
        Assertions.assertEquals(0, actual.size());
    }

    @ParameterizedTest
    @DisplayName("MoonService::getMoonByName - Success")
    @Order(2)
    @CsvSource({
            "1,Moon1",
            "1,Moon2",
            "2,Moon3",
            "2,Moon4"
    })
    public void getMoonByNameSuccess(int ownerId, String moonName) {
        Moon toReturn = new Moon();
        toReturn.setName(moonName);

        when(dao.getMoonByName(ownerId, moonName)).thenReturn(toReturn);
        Moon actual = moonService.getMoonByName(ownerId, moonName);
        boolean comparison = toReturn.getName().equals(moonName);
        Assertions.assertTrue(comparison);
    }

    @Test
    @DisplayName("MoonService::getMoonByName - Failure")
    @Order(3)
    public void getMoonByNameFailure() {
        int ownerId = 1;
        String moonName = "ThisMoonDoesNotExist";
        when(dao.getMoonByName(ownerId, moonName)).thenReturn(null);
        Moon actual = moonService.getMoonByName(ownerId, moonName);
        Assertions.assertNull(actual);
    }

    @ParameterizedTest
    @DisplayName("MoonService::getMoonById - Success")
    @Order(4)
    @CsvSource({
            "1,1",
            "1,2",
            "2,3",
            "2,4"
    })
    public void getMoonByIdSuccess(int ownerId, int moonId) {
        Moon toReturn = new Moon();
        toReturn.setId(moonId);

        when(dao.getMoonById(ownerId, moonId)).thenReturn(toReturn);
        Moon actual = moonService.getMoonById(ownerId, moonId);
        boolean comparison = toReturn.getId() == moonId;
        Assertions.assertTrue(comparison);
    }

    @Test
    @DisplayName("MoonService::getMoonById - Failure")
    @Order(5)
    public void getMoonByIdFailure() {
        int ownerId = 1;
        int moonId = -1;
        when(dao.getMoonById(ownerId, moonId)).thenReturn(null);
        Moon actual = moonService.getMoonById(ownerId, moonId);
        Assertions.assertNull(actual);
    }

    @ParameterizedTest
    @DisplayName("MoonService::createMoon - Success")
    @Order(6)
    @CsvSource({
            "1,Moon1",
            "1,Moon2",
            "2,Moon3",
            "2,Moon4"
    })
    public void createMoonSuccess(int ownerId, String moonName) {
        Moon toAdd = new Moon();
        toAdd.setName(moonName);

        when(dao.createMoon(ownerId, toAdd)).thenReturn(toAdd);
        Moon actual = moonService.createMoon(ownerId, toAdd);
        boolean conditions = toAdd.getName().equals(moonName);
        Assertions.assertTrue(conditions);
    }

    @Test
    @DisplayName("MoonService::createMoon - Failure - Empty Name")
    @Order(7)
    public void createMoonEmptyName() {
        int ownerId = 1;
        String moonName = "";
        Moon toAdd = new Moon();
        toAdd.setName(moonName);

        Moon actual = moonService.createMoon(ownerId, toAdd);
        Moon expected = new Moon();
        boolean condition = actual.getName() == null
                && actual.getId() == expected.getId()
                && actual.getMyPlanetId() == expected.getMyPlanetId();
        Assertions.assertTrue(condition);
    }

    @Test
    @DisplayName("MoonService::createMoon - Failure - Long Name")
    @Order(8)
    public void createMoonLongName() {
        int ownerId = 1;
        String moonName = "ThisIsMoreThan30Charactersaaaaa";
        Moon toAdd = new Moon();
        toAdd.setName(moonName);

        Moon actual = moonService.createMoon(ownerId, toAdd);
        Moon expected = new Moon();
        boolean condition = actual.getName() == null
                && actual.getId() == expected.getId()
                && actual.getMyPlanetId() == expected.getMyPlanetId();
        Assertions.assertTrue(condition);
    }

    @Test
    @DisplayName("MoonService::createMoon - Failure")
    @Order(9)
    public void createMoonSuccess() {
        int ownerId = 1;
        String moonName = "ThisFailsForSomeReason";
        Moon toAdd = new Moon();
        toAdd.setName(moonName);

        when(dao.createMoon(ownerId, toAdd)).thenReturn(new Moon());
        Moon actual = moonService.createMoon(ownerId, toAdd);
        Moon expected = new Moon();
        boolean condition = actual.getName() == null
                && actual.getId() == expected.getId()
                && actual.getMyPlanetId() == expected.getMyPlanetId();
        Assertions.assertTrue(condition);
    }

    @Test
    @DisplayName("MoonService::deleteMoon - Success")
    @Order(10)
    public void deleteMoonSuccess() {
        int ownerId = 1;
        int moonId = 1;
        when(dao.deleteMoonById(ownerId, moonId)).thenReturn(true);
        Assertions.assertTrue(moonService.deleteMoonById(ownerId, moonId));
    }

    @Test
    @DisplayName("MoonService::deleteMoon - Failure")
    @Order(11)
    public void deleteMoonFailure() {
        int ownerId = 1;
        int moonId = -1;
        when(dao.deleteMoonById(ownerId, moonId)).thenReturn(false);
        Assertions.assertFalse(moonService.deleteMoonById(ownerId, moonId));
    }

    @Test
    @DisplayName("MoonService::getMoonsFromPlanet - Success")
    @Order(12)
    public void getMoonsFromPlanetSuccess() {
        int ownerId = 1;
        int myPlanetId = 1;
        ArrayList<Moon> input = new ArrayList<Moon>();

        Moon m1 = new Moon();
        m1.setName("Moon 1");
        m1.setMyPlanetId(myPlanetId);
        m1.setId(1);
        input.add(m1);

        Moon m2 = new Moon();
        m2.setName("Moon 2");
        m2.setMyPlanetId(myPlanetId);
        m2.setId(2);
        input.add(m2);

        when(dao.getMoonsFromPlanet(ownerId, myPlanetId)).thenReturn(input);

        List<Moon> actual = moonService.getMoonsFromPlanet(ownerId, myPlanetId);
        Assertions.assertEquals(input, actual);
    }

    @Test
    @DisplayName("MoonService::getMoonsFromPlanet - Failure")
    @Order(13)
    public void getMoonsFromPlanetFailure() {
        int ownerId = 1;
        int myPlanetId = 1;
        ArrayList<Moon> input = new ArrayList<Moon>();

        when(dao.getMoonsFromPlanet(ownerId, myPlanetId)).thenReturn(input);

        List<Moon> actual = moonService.getMoonsFromPlanet(ownerId, myPlanetId);
        Assertions.assertEquals(0, actual.size());
    }
}
