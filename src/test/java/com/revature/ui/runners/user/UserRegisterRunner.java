package com.revature.ui.runners.user;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources/features/user/Register.feature"},
        glue = {"com.revature.ui.user.register"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/UserRegister.html"
        }
)
public class UserRegisterRunner {
}
