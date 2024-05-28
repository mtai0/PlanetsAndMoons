package com.revature.ui.user;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class UserRegisterSteps {
    @Given("User is on the Create Account page")
    public void userIsOnTheCreateAccountPage() {
    }

    @Given("no account exists with the username {string}")
    public void noAccountExistsWithTheUsername(String arg0) {
    }

    @And("both {string} and {string} are of valid length \\({int} < Length <= {int})")
    public void bothAndAreOfValidLengthLength(String arg0, String arg1, int arg2, int arg3) {
    }

    @When("User inputs the given account credentials {string} | {string}")
    public void userInputsTheGivenAccountCredentials(String arg0, String arg1) {
    }

    @And("User clicks on the registration button")
    public void userClicksOnTheRegistrationButton() {
    }

    @Then("account creation is successful")
    public void accountCreationIsSuccessful() {
    }

    @And("{string} is of invalid length \\(Length = {int} || Length > {int})")
    public void isOfInvalidLengthLengthLength(String arg0, int arg1, int arg2) {
    }

    @When("User inputs the given account {string} | {string}")
    public void userInputsTheGivenAccount(String arg0, String arg1) {
    }

    @Then("account creation fails")
    public void accountCreationFails() {
    }

    @Given("an account with the username {string} already exists")
    public void anAccountWithTheUsernameAlreadyExists(String arg0) {
    }
}
