package com.revature.ui.user.login;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources/features/user/Login.feature"},
        glue = {"com.revature.ui.user.login"},
        plugin = {
                "pretty",
                "html:src/test/resources/reports/html-reports-homepage.html"
        }
)
public class UserLoginRunner {
}
