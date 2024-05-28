Feature: Users can log in to the Planetarium
  Background: User already has an account and is on the login page.
    Given User has already created an account with the credentials username | password
    And User is on the Login page