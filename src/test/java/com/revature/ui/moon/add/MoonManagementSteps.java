package com.revature.ui.moon.add;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;

import com.revature.MainDriverTest;
import com.revature.models.Moon;
import com.revature.models.Planet;
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
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;

public class MoonManagementSteps {
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
         mooDao = new MoonDao();
        pdao = new PlanetDao();
        UserDao udao = new UserDao();
         UsernamePasswordAuthentication auth = new UsernamePasswordAuthentication();
        auth.setUsername("test");
        auth.setPassword("123456789");

        User actual = udao.createUser(auth);
        userid = actual.getId();
        Planet p = new Planet();
        p.setName("Earth");
        p.setOwnerId(actual.getId());
        Planet p2= new Planet();
        p2.setName("Mars");
        p2.setOwnerId(actual.getId());
        pdao.createPlanet(p);
        pdao.createPlanet(p2);
         
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
    @Given("User is on the Moon Management page")
    public void userIsOnTheMoonManagementPage() {
        // Simulate navigation to the Moon Management Page
        System.out.println("User is navigated to the Moon Management page.");
       
        loginPage.get();
        loginPage.enterCredentials("test", "123456789");
        loginPage.submit();
        
        home = PageFactory.initElements(driver, Homepage.class);
       home.isOnHomepage();
    
 
      
        
    }

    @Given("no moon exists with the name {string} on planet {string}")
    public void noMoonExistsWithTheNameOnPlanet(String moonName, String planetName) {
        // Check if the moon does not exist on the specified planet
        boolean exists = checkMoonExists(moonName, planetName);
        Assert.assertFalse("Moon already exists", exists);
    }

    @Given("a moon with the name {string} already exists on planet {string}")
    public void moonAlreadyExistsOnPlanet(String moonName, String planetName) {
        // Assert a moon already exists
        Moon tba= new Moon();
        tba.setName(moonName);
        Planet p=pdao.getPlanetByName(userid, planetName);
        tba.setMyPlanetId(p.getId());
        mooDao.createMoon(userid,tba);
        
        
    }

    @And("{string} is of valid length")
    public void moonNameIsOfValidLength(String moonName) {
        // Validate moon name length
        Assert.assertTrue("Moon name length is invalid", isValidLength(moonName));
    }

    @And("{string} is of invalid length")
    public void moonNameIsOfInvalidLength(String moonName) {
        // Validate moon name length
        Assert.assertFalse("Moon name length is valid", isValidLength(moonName));
    }

    @When("User inputs the moon details {string} | {string}")
    public void userInputsTheMoonDetails(String moonName, String planetName) {
        // Simulate user inputting moon details
        
        home.clickMoonSelector();
        home.inputMoonName(moonName);
        
        Planet temp=pdao.getPlanetByName(userid,planetName);
        home.inputOrbitedPlanet(""+temp.getId());
        System.out.println("User inputs moon details: " + moonName + " on " + planetName);
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
    }

    @And("User clicks on the add moon button")
    public void userClicksOnTheAddMoonButton() {
        // Simulate user clicking the add button
        home.clickSubmitButton();
        
        System.out.println("User clicks on the add moon button.");
    }

    @Then("moon creation is successful")
    public void moonCreationIsSuccessful() {
        // Assert moon creation success
       
        Assert.assertTrue(home.moonadded());
    }

    @Then("moon creation fails")
    public void moonCreationFails() {
        // Assert moon creation failure
        
        Assert.assertFalse(home.moonadded());
    }

    // Helper method to simulate database check
    private boolean checkMoonExists(String moonName, String planetName) {
        // Implement actual check from the database
        Moon test= mooDao.getMoonByName(userid,moonName);
        return test!=null;
    }

    // Helper method to check name length
    private boolean isValidLength(String name) {
        return name.length() > 0 && name.length() <= 30;
    }

    // Helper method to simulate creation result
    private boolean isMoonCreatedSuccessfully() {
        // Implement creation logic and return the result
        return true;
    }
}
