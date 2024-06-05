package com.revature.ui.moons.retrieve;

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
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;

public class ViewMoonsSteps {
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
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    
        
    }

    @Given("User has multiple moons under a planet {string}")
    public void userHasMultipleMoonsUnderAPlanet(String moonName) 
    {
        Moon moon = new Moon();
    
        moon.setName(moonName);
        Planet p= pdao.getPlanetByName(userid,"Earth");
        moon.setMyPlanetId(p.getId());
        mooDao.createMoon(userid,moon);
    }

     @When("User selects to search moon {string}")
    public void userSelectsToSearchMoon(String moonName) 
    {
        home.moonSearchInput(moonName);
        home.searchMoonButtonclick();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("User sees moon row with {string}")
    public void userSeesMoonRowWith(String moonName) 
    {
      
       Assert.assertTrue(home.moonsearched(moonName));
    }
    
}
