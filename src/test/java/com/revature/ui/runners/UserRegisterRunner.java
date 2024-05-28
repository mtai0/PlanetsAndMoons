package com.revature.ui.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources/features/user/Register.feature"},
        glue = {"com.revature.ui"},
        plugin = {
                "pretty",
                "html:src/test/resources/reports/html-reports-homepage.html"
        }
)
public class UserRegisterRunner {
}
