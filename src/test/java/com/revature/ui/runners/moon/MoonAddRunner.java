package com.revature.ui.runners.moon;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features/Moon/add.feature", // Path to your feature files
    glue = "com.revature.ui.moon.add", // Path to your step definitions
    plugin = {"pretty", "html:target/cucumber-reports/MoonAdd.html"} // Plugins for report generation
)
public class MoonAddRunner {
}
