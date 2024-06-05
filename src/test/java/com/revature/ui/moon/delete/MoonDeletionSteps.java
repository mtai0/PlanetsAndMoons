package com.revature.ui.moon.delete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;

import com.revature.MainDriverTest;
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

public class MoonDeletionSteps {
    private WebDriver driver;
    private Login loginPage;
    private Homepage home;
    private MoonDao mooDao;
    private PlanetDao pdao;
    private int userid;

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

    private void loginAndNavigateToMoonManagement() {
        loginPage.get();
        loginPage.enterCredentials("test", "123456789");
        loginPage.submit();
        Assert.assertTrue("Not on Home page", home.isOnHomepage());
    }

    @After
    public void tearDown() {
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

    @Given("a moon with the name {string} already exists on planet {string}")
    public void aMoonWithTheNameAlreadyExistsOnPlanet(String moonName, String planetName) {
        home.clickMoonSelector();
        home.inputMoonName(moonName);
        
        
        Planet temp=pdao.getPlanetByName(userid,planetName);
        home.inputOrbitedPlanet(""+temp.getId());
        System.out.println("User inputs moon details: " + moonName + " on " + planetName);
        home.clickSubmitButton();
        home.moonadded();
    }

    @Given("no moon exists with the name {string} on planet {string}")
    public void noMoonExistsWithTheNameOnPlanet(String moonName, String planetName) {
       
    }

    @When("User selects the moon {string} | {string}")
    public void userSelectsTheMoon(String moonName, String planetName) 
    {
            int exist=home.getMoonID(moonName);
          
           
            home.inputMoonIdForDeletion(exist);
            
    }

    @And("User clicks on the delete moon button")
    public void userClicksOnTheDeleteMoonButton() 
    {
        home.clickDeleteButton();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("moon deletion is successful")
    public void moonDeletionIsSuccessful() {
        home.moonDeleted();
       
    }

    @Then("moon deletion fails")
    public void moonDeletionFails() {
        
    }

    @Then("Nothing Happens")
    public void notthingHappens() {

    }
}
