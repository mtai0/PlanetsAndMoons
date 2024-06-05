package com.revature.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

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
    WebElement locationSelect;

    @FindBy(xpath = "//*[@id='inputContainer']//button[contains(text(), 'Submit Planet')]")
    private WebElement planetSubmitButton;
    @FindBy(id = "planetNameInput")
    private WebElement planetNameInput;

    @FindBy(id = "orbitedPlanetInput")
    private WebElement orbitedPlanetInput;


    @FindBy(xpath = "//*[@id=\"loginForm\"]/input[@value=\"Create\"]")
    private WebElement registerButton;

    @FindBy(id = "deleteInput")
    private WebElement deleteInput;


    @FindBy(id = "searchPlanetInput")
    private WebElement searchPlanetInput;

    @FindBy(xpath = "//*[@id=\"loginForm\"]/input[@value=\"Login\"]")
    private WebElement loginButton;
    @FindBy(id = "greeting")

    private WebElement greetingBtn;
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


        Select locationselectelement = new Select(locationSelect);

        locationselectelement.selectByVisibleText("Moon");


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


    public boolean isPlanetInTable(String bodyName) {
        System.out.println("Is Planet in Table: " + bodyName);

        List<WebElement> rows = celestialTable.findElements(By.tagName("tr"));

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (!cells.isEmpty()) {
                String type = cells.get(0).getText();
                String name = cells.get(2).getText();

                if ("planet".equals(type) && bodyName.equals(name)) {
                    return true;
                } else if ("moon".equals(type) && bodyName.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getPlanetIdByName(String bodyName) {
        List<WebElement> rows = celestialTable.findElements(By.tagName("tr"));

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (!cells.isEmpty()) {
                String type = cells.get(0).getText();
                String id = cells.get(1).getText();
                String name = cells.get(2).getText();
                String owner = cells.get(3).getText();

                if ("planet".equals(type) && bodyName.equals(name) || "moon".equals(type) && bodyName.equals(name)) {
                    try {
                        return Integer.parseInt(id);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        return -1; // Return -1 if the ID is not a valid integer
                    }
                }
            }
        }
        return -1; // Return -1 if the planet is not found
    }


    // Method to check which option is selected in locationSelect
    public String getSelectedLocationOption() {
        Select select = new Select(locationSelect);
        return select.getFirstSelectedOption().getText();
    }

    // Method to check if option 1 or option 2 is selected
    public boolean isOptionSelected(String optionText) {
        Select select = new Select(locationSelect);
        return select.getFirstSelectedOption().getText().equalsIgnoreCase(optionText);
    }

    // Method to select a desired option in locationSelect
    public void selectLocationOption(String optionText) {
        Select select = new Select(locationSelect);
        select.selectByVisibleText(optionText);
    }

    public void enterSearchPlanetInput(String input) {
        searchPlanetInput.sendKeys(input);
    }

    public void clickSearchPlanetButton() {
        searchPlanetButton.click();
    }
    public void clickDeleteButton() {
        deleteButton.click();
    }

    public void clickSubmitPlanetButton() {
        planetSubmitButton.click();
    }


    public void enterDeleteInput(String input) {
        deleteInput.sendKeys(input);
    }

    public void enterPlanetNameAddInput(String input) {
        planetNameInput.sendKeys(input);
    }

    public void enterPlanetIDAddInput(String input) {
        orbitedPlanetInput.sendKeys(input);
    }

}
