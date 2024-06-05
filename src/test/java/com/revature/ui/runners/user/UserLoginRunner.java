package com.revature.ui.runners.user;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources/features/user/Login.feature"},
        glue = {"com.revature.ui.user.login"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/UserLogin.html"
        }
)
public class UserLoginRunner {
}
