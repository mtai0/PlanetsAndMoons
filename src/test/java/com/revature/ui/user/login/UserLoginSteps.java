package com.revature.ui.user.login;

import com.revature.MainDriver;
import com.revature.MainDriverTest;
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
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserLoginSteps {
    private WebDriver driver;
    private Login loginPage;

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
    }

    @After
    public void cleanup() {
        resetDatabase();
        driver.quit();
    }

    @Given("User is on the Login page")
    public void userIsOnTheLoginPage() {
        loginPage.get();
    }

    @Given("User has already created an account with the credentials {string} | {string}")
    public void userHasAlreadyCreatedAnAccountWithTheCredentials(String username, String password) {
        try (Connection connection = ConnectionUtil.createConnection()) {
            PreparedStatement ps = connection.prepareStatement("insert into users (username, password) values (?, ?)");
            ps.setString(1, username);
            ps.setString(2, password);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected <= 0) Assertions.fail("userHasAlreadyCreatedAnAccountWithTheCredentials failed to populate database.");

        } catch(SQLException e) {
            Assertions.fail("userHasAlreadyCreatedAnAccountWithTheCredentials failed to populate database due to a SQLException.");
        }
    }

    @When("User enters account credentials {string} | {string}")
    public void userEntersAccountCredentials(String username, String password) {
        loginPage.enterCredentials(username, password);
    }

    @Given("username {string} is unregistered")
    public void usernameIsUnregistered(String username) {
        try(Connection connection = ConnectionUtil.createConnection()){
            String sql = "select * from users where username = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) Assertions.fail("usernameIsUnregistered - Account already Exists");
        } catch (SQLException e){
            Assertions.fail("usernameIsUnregistered failed due to SQLException.");
        }
    }

    @And("User clicks the login button")
    public void userClicksTheLoginButton() {
        loginPage.submit();
    }

    @Then("User fails to log in")
    public void userFailsToLogIn() {
        Assertions.assertTrue(loginPage.checkFail());
    }

    @Then("User is taken to their homepage")
    public void userIsTakenToTheirHomepage() {
        Homepage homepage = PageFactory.initElements(driver, Homepage.class);
        Assertions.assertTrue(homepage.isOnHomepage());
    }
}
