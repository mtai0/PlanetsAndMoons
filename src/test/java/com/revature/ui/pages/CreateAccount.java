package com.revature.ui.pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

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

    private final String url = "http://localhost:7000";
    public void get() {
        driver.get(url + "/webpage/create");
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

    public boolean checkSuccess() {
        boolean handled = false;

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.alertIsPresent());

        Alert alert;
        alert = driver.switchTo().alert();
        if (alert != null)
        {
            //alert.getText().contains("Account creation failed with username");
            handled = alert.getText().contains("Account created successfully with username");
            alert.dismiss();
        }

        return handled;
    }

    public boolean checkFail() {
        boolean handled = false;

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.alertIsPresent());

        Alert alert;
        alert = driver.switchTo().alert();
        if (alert != null)
        {
            handled = alert.getText().contains("Account creation failed with username");
            alert.dismiss();
        }

        return handled;
    }
}
