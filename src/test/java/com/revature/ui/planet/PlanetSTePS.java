package com.revature.ui.planet;

import com.revature.models.Planet;
import com.revature.service.PlanetService;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;

import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;

import com.revature.MainDriverTest;
import com.revature.models.User;
import com.revature.models.UsernamePasswordAuthentication;
import com.revature.repository.MoonDao;
import com.revature.repository.PlanetDao;
import com.revature.repository.UserDao;
import com.revature.ui.pages.Homepage;
import com.revature.ui.pages.Login;
import com.revature.utilities.ConnectionUtil;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.github.bonigarcia.wdm.WebDriverManager;
import static org.junit.jupiter.api.Assertions.*;

public class PlanetSTePS {
    private Login loginPage;
    private Homepage home;
    PlanetDao pdao;
    UserDao userDao;
    private PlanetService planetService;
    int userid;
    private User testUser;
    private WebDriver driver;
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

    @BeforeAll
    public static void startWebsite() {


        MainDriverTest.main(null);

    }

    @Before
    public void setup() {
        resetDatabase();
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        loginPage = PageFactory.initElements(driver, Login.class);

        home = PageFactory.initElements(driver, Homepage.class);
    }
    @After
    public void cleanup() {
        resetDatabase();
        driver.quit();
    }
    @Given("the user has an existing account")
    public void theUserHasAnExistingAccount() {
        userDao = new UserDao();
        testUser = new User();

        String username = "test";
        String password = "123456789";
        UsernamePasswordAuthentication user = new UsernamePasswordAuthentication();
        user.setUsername(username);
        user.setPassword(password);
        User actual = userDao.createUser(user);
        userid = actual.getId();
        if (userDao.getUserByUsername(username) == null) {
            testUser = userDao.createUser(user);
            assertNotNull(user);
            assertEquals(username, testUser.getUsername());
        }
    }

    @And("the user is logged in")
    public void theUserIsLoggedIn() {
        driver.get("http://localhost:7000/webpage/login");
        loginPage.enterCredentials("test","123456789");
        loginPage.waitForHomePageLoad();
    }

    @And("the user is on the home page")
    public void theUserIsOnTheHomePage() {
        String pageTitle = driver.getTitle();
        assertEquals("Home", pageTitle, "User is not directed to the Home page");

        // Check if ownerID is present in sessionStorage
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String ownerId = (String) js.executeScript("return sessionStorage.getItem('userId');");
        String userName = (String) js.executeScript("return sessionStorage.getItem('user');");
        assertTrue(ownerId != null && !ownerId.isEmpty(), "ownerID is not present in sessionStorage");
        assertEquals("testuser1", userName);

        testUser.setId(Integer.parseInt(ownerId));
        testUser.setUsername(userName);
    }

    @Given("the planet name {string} does not already exist")
    public void thePlanetDoesNotAlreadyExist(String planetName) {
        pdao = new PlanetDao();
        planetService = new PlanetService(pdao);
        assertNull(planetService.getPlanetByName(testUser.getId(), planetName));
    }

    @And("the Planet option is selected in the location select")
    public void thePlanetOptionIsSelectedInTheLocationSelect() {
        if (!home.isOptionSelected("Planet")) {
            home.selectLocationOption("Planet");
        }
        String selectedOption = home.getSelectedLocationOption();
        assertEquals(selectedOption, "Planet");
    }

    @When("the user enters {string} in the planet add input")
    public void theUserEntersInThePlanetInput(String planetName) {
        home.enterPlanetNameAddInput(planetName);
    }

    @And("clicks the submit planet button")
    public void clicksTheSubmitPlanetButton() {
        home.clickSubmitPlanetButton();
    }

    @Then("the planet name {string} should be added successfully to the Celestial Table")
    public void thePlanetShouldBeAddedSuccessfullyToTheCelestialTable(String planetName) throws InterruptedException {
        Thread.sleep(500);
        pdao = new PlanetDao();
        planetService = new PlanetService(pdao);
        Planet planet = planetService.getPlanetByName(testUser.getId(), planetName);

        // Assert that the method returns a planet
        assertNotNull(planet, "Planet should be found by the service");

        // Query the table to check for the matching row
        boolean isPlanetInTable = home.isPlanetInTable(planet.getName());

        // Assert that the planet is found in the table
        assertTrue(isPlanetInTable, "Planet should be found in the celestial table");
    }


    @Given("the planet name {string} already exists")
    public void thePlanetNameAlreadyExists(String planetName) {
        assertTrue(home.isPlanetInTable(planetName.trim().toLowerCase()));
    }


    @When("the user enters planet ID to delete {string} in the delete planet input")
    public void theUserEntersPlanetIDInTheDeletePlanetInput(String planetName) {
        int planetID = home.getPlanetIdByName(planetName);
        pdao = new PlanetDao();
        planetService = new PlanetService(pdao);
        home.enterDeleteInput(String.valueOf(planetID));
    }

    @And("clicks the delete button")
    public void clicksTheDeleteButton() throws InterruptedException {
        Thread.sleep(2000);
        home.clickDeleteButton();
    }

    @Then("the alert should be displayed for Planet {string} Deleted Successfully")
    public void theAlertShouldBeDisplayedForPlanetDeleteSuccess(String planetName) throws InterruptedException {
        Thread.sleep(2000);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        String alertText = wait.until(ExpectedConditions.alertIsPresent()).getText();
        assertTrue(alertText.contains("Deleted planet with ID"), "Delete success alert not displayed");
        driver.switchTo().alert().accept();
        assertFalse(home.isPlanetInTable(planetName.trim().toLowerCase()));
    }

    @Then("the Error alert should be displayed")
    public void theErrorAlertShouldBeDisplayed() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        String alertText = wait.until(ExpectedConditions.alertIsPresent()).getText();
        assertTrue(alertText.contains("Failed"), "Error alert not displayed");
        driver.switchTo().alert().accept();
    }

    @When("the user enters {string} in the delete planet input")
    public void theUserEntersInTheDeletePlanetInput(String planetName) {
        home.enterDeleteInput(planetName.trim().toLowerCase());
    }

    @And("clicks the search planet button")
    public void clicksTheSearchPlanetButton() {
        home.clickSearchPlanetButton();
    }

    @When("the user enters {string} in the search planet input")
    public void theUserEntersPlanetNameInTheSearchPlanetInput(String planetName) {
        home.enterSearchPlanetInput(planetName.trim().toLowerCase());
    }

    @Then("the celestial table displays the {string}")
    public void theCelestialTableDisplaysThePlanetName(String planetName) {
        assertTrue(home.isPlanetInTable(planetName.trim().toLowerCase()));
    }

    @And("the user enters planet ID instead of {string} in the search planet input")
    public void theUserEntersPlanetIDInsteadOfInTheSearchPlanetInput(String planetName) {
        int planetID = home.getPlanetIdByName(planetName);
        home.enterSearchPlanetInput(String.valueOf(planetID));
    }
}
