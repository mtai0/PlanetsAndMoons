package com.revature.ui.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Homepage {
    private WebDriver driver;

    String moonName;
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

    private final String url = "http://localhost:7000";

    public void get() {
        driver.get(url + "/api/webpage/home");
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
  
    public void clickMoonSelector()
    {
      

         Select locationSelect = new Select(locationSelectElement);
       
        locationSelect.selectByVisibleText("Moon");
       
        
    }

    //for add moon
      public void inputMoonName(String moonName) {
        this.moonName=moonName;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement moonNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("moonNameInput")));
        moonNameInput.sendKeys(moonName);
    }

    // Method to input orbited planet ID
    public void inputOrbitedPlanet(String planetId) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement orbitedPlanetInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("orbitedPlanetInput")));
        orbitedPlanetInput.sendKeys(planetId);
    }


       public boolean moonadded() {
        if(moonName=="")
        {
            return false;
        }
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        By moonLocator = By.xpath("//table[@id='celestialTable']//tr[td[contains(text(), '" + moonName + "')]]");
         try{
        return wait.until(ExpectedConditions.visibilityOfElementLocated(moonLocator)) != null;
         }
         catch(Exception e){
             return false;
         }
    }

    public boolean moonsearched(String moon) {
        if(moon.equals(""))
        {
            return false;
        }
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        By moonLocator = By.xpath("//table[@id='celestialTable']//tr[td[contains(text(), '" + moon + "')]]");
         try{
        return wait.until(ExpectedConditions.visibilityOfElementLocated(moonLocator)) != null;
         }
         catch(Exception e){
             return false;
         }
    }

    public boolean moonDeleted() {
        if(moonName.equals(""))
        {
            return true;
        }
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        By moonLocator = By.xpath("//table[@id='celestialTable']//tr[td[contains(text(), '" + moonName + "')]]");
         try{
        return wait.until(ExpectedConditions.visibilityOfElementLocated(moonLocator)) != null;
         }
         catch(Exception e){
             return true;
         }
    }

    public void moonSearchInput(String moonName)
    {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOf(searchMoonText));  // Ensure the element is visible
        searchMoonText.clear();  // Clear any existing text
        searchMoonText.sendKeys(moonName);  // Enter the text
    }
    public void searchMoonButtonclick()
    {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(searchMoonButton));  // Ensure the button is clickable
        searchMoonButton.click(); 
    }

    public int getMoonID(String name) {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement table = wait.until(ExpectedConditions.visibilityOf(celestialTable));
            List<WebElement> rows = table.findElements(By.tagName("tr"));

            for (WebElement row : rows) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (!cells.isEmpty() && cells.get(2).getText().equals(moonName)) {  // Assuming the moon name is in the third cell
                    try {
                        return Integer.parseInt(cells.get(1).getText());  // Assuming the ID is in the second cell
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing moon ID: " + e.getMessage());
                        return -1;  // Return -1 or any other error code to indicate failure
                    }
                }
            }
            return -1;  // Moon not found
    }

    public void inputMoonIdForDeletion(int moonId) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOf(deleteText));  // Wait until the deleteText element is visible
    
        deleteText.clear();  // Clear any existing content in the input field
        deleteText.sendKeys(String.valueOf(moonId));  // Input the moon ID as a string

        
    
        System.out.println("Moon ID " + moonId + " entered for deletion.");
    }
    public void clickDeleteButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.elementToBeClickable(deleteButton));  // Ensure the button is clickable
        deleteButton.click();  // Click the delete button
    }


    //for add planet
    public void inputPlanetName(String planetName2) {
        this.planetName=planetName2;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement moonNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("planetNameInput")));
        moonNameInput.sendKeys(planetName);
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

    public boolean PlanetSearched(String planetName)
    {
        if(planetName=="")
        {
            return true;
        }
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        By planetlocator = By.xpath("//table[@id='celestialTable']//tr[td[contains(text(), '" + planetName + "')]]");
        try{
            return wait.until(ExpectedConditions.visibilityOfElementLocated(planetlocator)) != null;
        }
        catch(Exception e){
            return true;
        }
    }
    public void clickPlanetSelector()
    {


        Select locationSelect = new Select(locationSelectElement);

        locationSelect.selectByVisibleText("Planet");


    }

    public boolean planetDeleted(){
        if(planetName.equals(""))
        {
            return true;
        }
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        By planetlocator = By.xpath("//table[@id='celestialTable']//tr[td[contains(text(), '" + planetName + "')]]");
        try{
            return wait.until(ExpectedConditions.visibilityOfElementLocated(planetlocator)) != null;
        }
        catch(Exception e){
            return true;
        }
    }



    public void planetsearchINput(String planetName)
    {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOf(searchPlanetText));  // Ensure the element is visible
        searchPlanetText.clear();  // Clear any existing text
        searchPlanetText.sendKeys(planetName);  // Enter the text
    }
    public void setSearchPlanetButton()
    {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(searchPlanetButton));  // Ensure the button is clickable
        searchPlanetButton.click();
    }

    public int getPlanetID(String planetName) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement table = wait.until(ExpectedConditions.visibilityOf(celestialTable));
        List<WebElement> rows = table.findElements(By.tagName("tr"));

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (!cells.isEmpty() && cells.get(2).getText().equals(planetName)) {  // Assuming the moon name is in the third cell
                try {
                    return Integer.parseInt(cells.get(1).getText());  // Assuming the ID is in the second cell
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing planet ID: " + e.getMessage());
                    return -1;  // Return -1 or any other error code to indicate failure
                }
            }
        }
        return -1;  // Moon not found
    }

    public void inputPlanetIDforDeletion(int planetID) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOf(deleteText));  // Wait until the deleteText element is visible

        deleteText.clear();  // Clear any existing content in the input field
        deleteText.sendKeys(String.valueOf(planetID));  // Input the moon ID as a string



        System.out.println("Planet ID " + planetID + " entered for deletion.");
    }


}
