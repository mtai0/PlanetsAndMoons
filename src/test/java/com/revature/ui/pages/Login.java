package com.revature.ui.pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

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


    @FindBy(xpath = "//*[@id=\"loginForm\"]/input[@value=\"Create\"]")
    private WebElement registerButton;

    @FindBy(xpath = "//*[@id=\"loginForm\"]/input[@value=\"Login\"]")
    private WebElement loginButton;
    @FindBy(id = "greeting")
    private WebElement greetingBtn;
    public Login(WebDriver driver) {
        this.driver = driver;
    }

    public void get() {
        driver.get("http://localhost:7000/webpage/login");
    }

    public void enterCredentials(String username, String password) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.elementToBeClickable(usernameText)).sendKeys(username);
        wait.until(ExpectedConditions.elementToBeClickable(passwordText)).sendKeys(password);
    }

    public void submit() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.elementToBeClickable(submitButton)).click();
    }

    public boolean checkFail() {
        boolean handled = false;

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.alertIsPresent());

        Alert alert;
        alert = driver.switchTo().alert();
        if (alert != null)
        {
            handled = true;
            //alert.getText().contains("login attempt failed: please try again");
            alert.dismiss();
        }

        return handled;
    }



}
