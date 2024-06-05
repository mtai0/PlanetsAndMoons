package com.revature.ui.runners.moon;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features/Moon/retrieve.feature", // Path to your feature files
    glue = "com.revature.ui.moon.retrieve", // Path to your step definitions
    plugin = {"pretty", "html:target/cucumber-reports/MoonRetrieve.html"} // Plugins for report generation
)
public class MoonRetrieveRunner
{
    
}
