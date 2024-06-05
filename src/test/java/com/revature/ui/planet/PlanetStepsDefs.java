package com.revature.ui.planet;

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
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlanetStepsDefs {
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

    @Given("User is on the Planet Management page")
    public void userIsOnThePlanetManagementPage() {
        loginPage.get();
        loginPage.enterCredentials("test", "123456789");
        loginPage.submit();

        home = PageFactory.initElements(driver, Homepage.class);
        home.isOnHomepage();

    }

    @Given("no planet exists with {string} name")
    public void noPlanetExistsWithName(String planetName) {
        boolean exists = checkplanetExists( planetName);
        Assert.assertFalse("Planet already exists", exists);

    }

    @And("{string} is of valid length")
    public void isOfValidLength(String planet) {
        Assert.assertTrue("planet name length is invalid", isValidLength(planet));
    }

    @And("User clicks on the add planet button")
    public void userClicksOnTheAddPlanetButton() {

    }

    @Then("planet creation is successful")
    public void planetCreationIsSuccessful() {
    }

    @Given("no planet {string} exists")
    public void noPlanetExists(String arg0) {
    }

    @And("{string} is of invalid length")
    public void isOfInvalidLength(String planet) {
        Assert.assertFalse("planet name length is valid", isValidLength(planet));
    }

    @And("User clicks on the add planet button to")
    public void userClicksOnTheAddPlanetButtonTo() {

    }

    @Then("planet creation fails")
    public void planetCreationFails() {

    }

    @Given("a planet with the name {string}  exists")
    public void aPlanetWithTheNameExists(String arg0) {
        Planet tba= new Planet();
        tba.setName(arg0);
        Planet p=pdao.getPlanetByName(userid, arg0);
        //tba.setMyPlanetId(p.getId());
        pdao.createPlanet(p);
    }
}
