package com.revature.ui.planets;

import com.revature.models.Planet;
import com.revature.service.PlanetService;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.hc.core5.util.Asserts;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;

import com.revature.MainDriverTest;
import com.revature.models.User;
import com.revature.models.UsernamePasswordAuthentication;
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
        boolean exists = checkPlanetExists( planetName);
        Assert.assertFalse("Planet already exists", exists);
    }

    @And("{string} is of valid length")
    public void isOfValidLength(String planetname) {
        boolean planetnameValid = planetname.length() <= 30 && !planetname.isEmpty();
        if (!planetnameValid) Assertions.fail("Planetname is of INVALID length.");
    }

    @When("the planet details {string} inputted")
    public void thePlanetDetailsInputted(String planet) {
        home.clickPlanetSelector();
        home.inputPlanetName(planet);

        //  Planet temp=pdao.getPlanetByName(userid,planetName);
        //  home.inputOrbitedPlanet(""+temp.getId());
        System.out.println("User inputs Planet details: " + planet);
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
    }

    @And("add planet button is clicked")
    public void addPlanetButtonIsClicked() {
        home.clickSubmitButton();
    }

    @Then("creation of planet is sucessful")
    public void creationOfPlanetIsSucessful() {
        Assert.assertTrue(home.planetAdded());
    }

    @Then("creation  of planet is sucessful")
    public void creation_of_planet_is_sucessful() {
        // Write code here that turns the phrase above into concrete actions
        Assert.assertTrue(home.planetAdded());
    }

    @Given("no planet {string} exists")
    public void noPlanetExists(String planetName) {

        boolean exists = checkPlanetExists( planetName);
        Assert.assertFalse("Planet already exists", exists);

    }

    @And("{string} is of invalid length")
    public void isOfInvalidLength(String planet) {
        boolean planetNameValid = planet.length() <= 30 && !planet.isEmpty();
        Assertions.assertFalse(planetNameValid);
    }

    @When("inputs the planet details  {string}")
    public void inputs_the_planet_details(String planet) {
        // Write code here that turns the phrase above into concrete actions
        home.clickPlanetSelector();
        home.inputPlanetName(planet);

        //  Planet temp=pdao.getPlanetByName(userid,planetName);
        //  home.inputOrbitedPlanet(""+temp.getId());
        System.out.println("User inputs Planet details: " + planet);
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
    }
    @When("clicks on the add planet button to")
    public void clicks_on_the_add_planet_button_to() {
        // Write code here that turns the phrase above into concrete actions
        home.clickSubmitButton();
    }


    @And("clicks on the add planet  button to {string} ")
    public void clicksOnTheAddPlanetButtonTo(String planet) {
        home.clickPlanetSelector();
        home.inputPlanetName(planet);

        //  Planet temp=pdao.getPlanetByName(userid,planetName);
        //  home.inputOrbitedPlanet(""+temp.getId());
        System.out.println("User inputs Planet details: " + planet);
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }

        home.clickSubmitButton();
    }

    @Then("planet creation fails")
    public void planetCreationFails() {
        Assert.assertFalse(home.planetAdded());
    }

    @Given("a planet with the name {string}  exists")
    public void aPlanetWithTheNameExists(String planet) {
        boolean exists = checkPlanetExists( planet);
        Assert.assertTrue("Planet already exists", exists);
    }

    @When("User inputs the  details {string}")
    public void user_inputs_the_details(String string) {
        // Write code here that turns the phrase above into concrete actions
        home.clickPlanetSelector();
        home.inputPlanetName(string);

        //  Planet temp=pdao.getPlanetByName(userid,planetName);
        //  home.inputOrbitedPlanet(""+temp.getId());
        System.out.println("User inputs Planet details: " + string);
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        };
    }
    @And("clicks on the add button")
    public void clicksOnTheAddButton() {
        home.clickSubmitButton();
    }


    @Then("creation fails")
    public void creationFails() {

        Assert.assertTrue(home.planetAdded());
    }
    @Given("no planet {string} {string} exists")
    public void no_planet_exists(String string, String string2) {
        // Write code here that turns the phrase above into concrete actions
        boolean exists = checkPlanetExists( string);
        Assert.assertFalse("Planet already exists", exists);
    }
    @Given("{string} {string} is of invalid length")
    public void is_of_invalid_length(String string, String planetname) {
        // Write code here that turns the phrase above into concrete actions
        boolean planetNameValid = planetname.length() <= 30 && !planetname.isEmpty();
        Assertions.assertFalse(planetNameValid);
    }
    @When("inputs the planet details  {string} {string}")
    public void inputs_the_planet_details(String string, String string2) {
        // Write code here that turns the phrase above into concrete actions
        // Write code here that turns the phrase above into concrete actions
        home.clickPlanetSelector();
        home.inputPlanetName(string);

        //  Planet temp=pdao.getPlanetByName(userid,planetName);
        //  home.inputOrbitedPlanet(""+temp.getId());
        System.out.println("User inputs Planet details: " + string);
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        };
    }
    @Given("the planetname {string} exists")
    public void the_planetname_exists(String string) {
        // Write code here that turns the phrase above into concrete actions
        boolean exists = checkPlanetExists( string);
        Assert.assertTrue("Planet already exists", exists);
}
    @When("User selects to search for planet {string} and inputs it in as input")
    public void user_selects_to_search_for_planet_and_inputs_it_in_as_input(String string) {
        // Write code here that turns the phrase above into concrete actions
       home.planetsearchINput(string);
       home.setSearchPlanetButton();
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Then("User sees planet row with {string}")
    public void user_sees_planet_row_with(String string) {
        // Write code here that turns the phrase above into concrete actions
        Assert.assertTrue(home.PlanetSearched(string));
    }

    //delete planets section
    @And("User clicks on the delete planet button")
    public void userClicksOnTheDeletePlanetButton() {
        home.clickDeleteButton();
        try{
            Thread.sleep(1000);
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Then("planet deletion is successful")
    public void planetDeletionIsSuccessful() {
        home.planetDeleted();
    }

    @Then("Nothing happens")
    public void nothingHappens() {
      System.out.println("nothign happens");
    }
    @Given("a planet with the name {string} already exists")
    public void aPlanetWithTheNameAlreadyExists(String arg0) {
        home.clickPlanetSelector();
        home.inputPlanetName(arg0);

        home.clickSubmitButton();
        home.planetAdded();

    }

    @When("User selects  planet <planetName>'")
    public void userSelectsPlanetPlanetName(String planetName) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        // throw new cucumber.api.PendingException();}

        int exist = home.getPlanetID(planetName);
        home.inputPlanetIDforDeletion(exist);

    }

    @When("User selects  planet \"Mars'")
    public void user_selects_planet_mars() {
        // Write code here that turns the phrase above into concrete actions
        Planet earth = pdao.getPlanetByName(userid,"Mars");
        int exist = home.getPlanetID(earth.getName());
        home.inputPlanetIDforDeletion(exist);
    }


    @When("User selects  planet \"Earth'")
    public void user_selects_planet_earth() {
        // Write code here that turns the phrase above into concrete actions
       Planet earth = pdao.getPlanetByName(userid,"Earth");
      int exist = home.getPlanetID(earth.getName());
      home.inputPlanetIDforDeletion(exist);
    }

    @Given("no planet exists with the name {string}")
    public void no_planet_exists_with_the_name(String string) {
        // Write code here that turns the phrase above into concrete actions
        boolean exists = checkPlanetExists( string);
        Assert.assertFalse("Planet doesnt already exists", exists);
    }
    @When("User selects the planet {string}")
    public void user_selects_the_planet(String string) {
        // Write code here that turns the phrase above into concrete actions
        int exist = home.getPlanetID(string);
        home.inputPlanetIDforDeletion(exist);
    }
    private boolean checkPlanetExists( String planetName) {
        // Implement actual check from the database
        Planet  test= pdao.getPlanetByName(userid,planetName);

        return test!=null;

    }



}


