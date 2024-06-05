package com.revature.ui.planet;

import com.revature.models.Planet;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.github.bonigarcia.wdm.WebDriverManager;
public class PlanetSTePS {
    private Login loginPage;
    private Homepage home;
    PlanetDao pdao;
    UserDao userDao;
    MoonDao mooDao;
    int userid;
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

        pdao = new PlanetDao();
        UserDao udao = new UserDao();
        UsernamePasswordAuthentication auth = new UsernamePasswordAuthentication();
        auth.setUsername("test");
        auth.setPassword("123456789");

        User actual = udao.createUser(auth);
        userid = actual.getId();
//        Planet p = new Planet();
//        p.setName("Earth");
//        p.setOwnerId(actual.getId());
//        Planet p2= new Planet();
//        p2.setName("Mars");
//        p2.setOwnerId(actual.getId());
//        pdao.createPlanet(p);
//        pdao.createPlanet(p2);

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        loginPage = PageFactory.initElements(driver, Login.class);
    }
    @After
    public void cleanup() {
        resetDatabase();
        driver.quit();
    }


    @Given("user is logged in and on the planet management page")
    public void userIsLoggedInAndOnThePlanetManagementPage() {
        // Simulate navigation to the Moon Management Page
        System.out.println("User is navigated to the Moon Management page.");

        loginPage.get();
        loginPage.enterCredentials("test", "123456789");
        loginPage.submit();

        home = PageFactory.initElements(driver, Homepage.class);
        home.isOnHomepage();

    }

    @Given("the planet name {string} does not already exist")
    public void thePlanetNameDoesNotAlreadyExist(String planetName) {

        boolean exists = checkifPlanetExists(planetName);
        Assert.assertTrue("Planet doesNotalready exists", exists);

    }

    @And("{string} is of valid length and not empty")
    public void isOfValidLengthAndNotEmpty(String planetName) {

        //validate that string is >30 and not empty
        Assert.assertTrue("Planetname length is valid", isValidLength(planetName));
    }

    @When("the user enters {string} in the planet add input")
    public void theUserEntersInThePlanetAddInput(String planetName) {
        home.clickPlanetSelector();
        home.inputPlanetName(planetName);

        //Planet temp=pdao.getPlanetByName(userid,planetName);

        System.out.println("User inputs planet details: " + planetName );
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
    }

    @And("clicks the submit planet button")
    public void clicksTheSubmitPlanetButton() {
        home.clickSubmitButton();
    }

    @Then("the planet name {string} is added successfully to the Celestial Table")
    public void thePlanetNameIsAddedSuccessfullyToTheCelestialTable(String arg0) {
    }

    @Given("planet name {string} isnt empty")
    public void planetNameIsntEmpty(String planet) {
        home.clickPlanetSelector();

       if(planet.isBlank() || planet.length()>30) {
           home.inputPlanetName(planet);

           //Planet temp=pdao.getPlanetByName(userid,planetName);

           System.out.println("User inputs planet details: " + planet);
           try {
               Thread.sleep(1000);
           } catch (Exception e) {
           }
       }
       Assert.assertFalse(home.planetAdded());
    }

    @And("User clicks on planet Submit button")
    public void userClicksOnPlanetSubmitButton() {
        home.clickSubmitButton();
    }

    @Then("fails for planet")
    public void failsForPlanet() {
        Assert.assertFalse(home.planetAdded());
    }

    @Given("<planetName> is not empty and not of invalid length")
    public void planetnameIsNotEmptyAndNotOfInvalidLength(String planetName) {
        Assert.assertFalse("Planetname invalid alreadyExists", isValidLength(planetName));
    }

    @When("planet details {string} are inputted")
    public void planetDetailsAreInputted(String planet) {
        home.clickPlanetSelector();

        if(planet.isBlank() || planet.length()>30) {
            home.inputPlanetName(planet);

            //Planet temp=pdao.getPlanetByName(userid,planetName);

            System.out.println("User inputs planet details: " + planet);
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
        Assert.assertFalse(home.planetAdded());
    }

    @And("User clicks on the add planet button")
    public void userClicksOnTheAddPlanetButton() {
        home.clickSubmitButton();
    }

    @Then("Planet Creation Fails")
    public void planetCreationFails() {

        Assert.assertFalse(home.planetAdded());
    }


    // Helper method to simulate database check
    private boolean checkifPlanetExists(String planetName) {
        // Implement actual check from the database
        Planet test= pdao.getPlanetByName(userid,planetName);
        return test!=null;
    }

    // Helper method to check name length
    private boolean isValidLength(String name) {
        return name.length() <= 30 && !name.isBlank();
    }

    // Helper method to simulate creation result
    private boolean isPlanetCreated() {
        // Implement creation logic and return the result
        return true;
    }
}
