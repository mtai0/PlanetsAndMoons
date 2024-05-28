package com.revature.ui.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CreateAccount {
    private WebDriver driver;

    @FindBy(id = "usernameInput")
    WebElement usernameText;

    @FindBy(id = "passwordInput")
    WebElement passwordText;

    @FindBy(css =  "input[type='submit']")
    WebElement submitButton;

    public CreateAccount(WebDriver driver) {
        this.driver = driver;
    }

    public void get() {
        driver.get("http://localhost:7000/webpage/create");
    }
}
