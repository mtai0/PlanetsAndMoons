package com.revature.ui.moons.retrieve;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features/Moons/retrieve.feature", // Path to your feature files
    glue = "com.revature.ui.moons.retrieve", // Path to your step definitions
    plugin = {"pretty", "html:target/cucumber-reports"} // Plugins for report generation
)
public class RetRunner
{
    
}
