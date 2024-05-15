package com.revature.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Login {
    private WebDriver driver;

    @FindBy(id = "usernameInput")
    WebElement usernameText;

    @FindBy(id = "passwordInput")
    WebElement passwordText;

    @FindBy(css =  "input[type='submit']")
    WebElement submitButton;

    @FindBy(xpath =  "//a[contains(@href, 'create')]")
    WebElement createAccountLink;

    public Login(WebDriver driver) {
        this.driver = driver;
    }

    public void get() {
        driver.get("http://localhost:7000/webpage/login");
    }
}
