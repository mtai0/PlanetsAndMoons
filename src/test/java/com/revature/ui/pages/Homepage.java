package com.revature.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Homepage {
    private WebDriver driver;
    String planetName;
    @FindBy(id = "deleteInput")
    WebElement deleteText;

    @FindBy(id = "deleteButton")
    WebElement deleteButton;

    @FindBy(id = "searchPlanetInput")
    WebElement searchPlanetText;

    @FindBy(id = "searchPlanetButton")
    WebElement searchPlanetButton;

    @FindBy(id = "searchMoonInput")
    WebElement searchMoonText;

    @FindBy(id = "searchMoonButton")
    WebElement searchMoonButton;

    @FindBy(id = "celestialTable")
    WebElement celestialTable;

    @FindBy(id = "locationSelect")
    WebElement locationSelectElement;

    //Functionality of certain elements changed based on whether it is set to Planet or Moon.
    private enum SiteSetting {
        PLANET, MOON
    }
    private SiteSetting currentSetting;

    public Homepage(WebDriver driver) {
        this.driver = driver;
    }

    public void get() {
        driver.get("http://localhost:7000/api/webpage/home");
        currentSetting = SiteSetting.PLANET;    //This is the default, but be sure to actually read-in the real value.
    }

    public boolean isOnHomepage() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.titleContains("Home"));
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public void clickPlanetSelector()
    {


        Select locationSelect = new Select(locationSelectElement);

        locationSelect.selectByVisibleText("Moon");


    }

    public void inputPlanetName(String planetName) {
        this.planetName=planetName;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement planetInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("planetNameInput")));
        planetInput.sendKeys(planetName);
    }



    public void clickSubmitButton() {
        WebDriverWait wait = new WebDriverWait(driver,  Duration.ofSeconds(5));
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("submit-button")));
        submitButton.click();


    }
    public boolean planetAdded() {
        if(planetName=="")
        {
            return false;
        }
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        By planetLocator = By.xpath("//table[@id='celestialTable']//tr[td[contains(text(), '" + planetName + "')]]");
        try{
            return wait.until(ExpectedConditions.visibilityOfElementLocated(planetLocator)) != null;
        }
        catch(Exception e){
            return false;
        }
    }
}
