Feature: Users can log in to the Planetarium
  Background: User already has an account and is on the login page.
    Given User is on the Login page

  Scenario: Users can log in if they provide the correct credentials
    Given User has already created an account with the credentials "username" | "password"
    When User enters account credentials "username" | "password"
    And User clicks the login button
    Then User is taken to their homepage

  Scenario: Users cannot log in if they provide the wrong password
    Given User has already created an account with the credentials "username" | "password"
    When User enters account credentials "username" | "wrongPassword"
    And User clicks the login button
    Then User fails to log in

  Scenario: Users cannot log in if they leave the password field blank
    Given User has already created an account with the credentials "username" | "password"
    When User enters account credentials "username" | ""
    And User clicks the login button
    Then User fails to log in

  Scenario: Users cannot log in if they leave the username field blank
    Given User has already created an account with the credentials "username" | "password"
    When User enters account credentials "" | "password"
    And User clicks the login button
    Then User fails to log in

  Scenario: Users cannot log in if they leave all fields blank
    Given User has already created an account with the credentials "username" | "password"
    When User enters account credentials "" | ""
    And User clicks the login button
    Then User fails to log in

  Scenario: Users cannot log in if they provide an unregistered username
    Given username "newUser" is unregistered
    When User enters account credentials "newUser" | "password"
    And User clicks the login button
    Then User fails to log in
