package com.revature.ui.user.register;

import com.revature.MainDriverTest;
import com.revature.ui.pages.CreateAccount;
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

public class UserRegisterSteps {
    private WebDriver driver;
    private CreateAccount createAccountPage;

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

        createAccountPage = PageFactory.initElements(driver, CreateAccount.class);
    }

    @After
    public void cleanup() {
        resetDatabase();
        driver.quit();
    }

    @Given("User is on the Create Account page")
    public void userIsOnTheCreateAccountPage() {
        createAccountPage.get();
    }

    @Given("no account exists with the username \"{string}\"")
    public void noAccountExistsWithTheUsername(String username) {
        try(Connection connection = ConnectionUtil.createConnection()){
            String sql = "select * from users where username = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) Assertions.fail("noAccountExistsWithTheUsername - Account already Exists");
        } catch (SQLException e){
            Assertions.fail("noAccountExistsWithTheUsername failed due to SQLException.");
        }
    }

    @And("both \"{string}\" and \"{string}\" are of valid length")
    public void bothAndAreOfValidLength(String username, String password) {
        boolean usernameValid = username.length() <= 30 && !username.isEmpty();
        if (!usernameValid) Assertions.fail("Username is of INVALID length.");

        boolean passwordValid = password.length() <= 30 && !password.isEmpty();
        if (!passwordValid) Assertions.fail("Password is of INVALID length.");
    }

    @When("User inputs the given account credentials \"{string}\" | \"{string}\"")
    public void userInputsTheGivenAccountCredentials(String username, String password) {
        createAccountPage.enterCredentials(username, password);
    }

    @And("User clicks on the registration button")
    public void userClicksOnTheRegistrationButton() {
        createAccountPage.submit();
    }

    @Then("account creation is successful")
    public void accountCreationIsSuccessful() {
        Assertions.assertTrue(createAccountPage.checkSuccess());
    }

    @And("\"{string}\" is of invalid length")
    public void isOfInvalidLength(String str) {
        boolean isInvalid = str.isEmpty() || str.length() > 30;
        if (!isInvalid) Assertions.fail("String is of VALID length. (Expected: INVALID)");
    }

    @Then("account creation fails")
    public void accountCreationFails() {
        Assertions.assertTrue(createAccountPage.checkFail());
    }

    @Given("an account with the username \"{string}\" already exists")
    public void anAccountWithTheUsernameAlreadyExists(String username) {
        try (Connection connection = ConnectionUtil.createConnection()) {
            PreparedStatement ps = connection.prepareStatement("insert into users (username, password) values (?, ?)");
            ps.setString(1, username);
            ps.setString(2, "password");
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected <= 0) Assertions.fail("anAccountWithTheUsernameAlreadyExists failed to populate database.");

        } catch(SQLException e) {
            Assertions.fail("anAccountWithTheUsernameAlreadyExists failed to populate database due to a SQLException.");
        }
    }
}
