package com.revature.ui.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Homepage {
    private WebDriver driver;

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
}
