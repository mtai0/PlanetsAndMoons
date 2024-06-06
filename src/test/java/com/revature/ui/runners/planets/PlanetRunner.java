package com.revature.ui.runners.planets;


import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources/features/Planet/Planet.feature","src/test/resources/features/Planet/SearchPlanet.feature","src/test/resources/features/Planet/planetdelete.feature" },// Path to your feature files
        glue = "com.revature.ui.planets", // Path to your step definitions
        plugin = {"pretty", "html:target/cucumber-reports"} // Plugins for report generation
)
public class PlanetRunner {
}
