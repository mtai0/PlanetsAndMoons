Feature: Users can delete moons in the Planetarium
  Background: User is logged in and viewing their list of moons.

  Scenario: Users can delete a moon
    Given User has moon "MoonName" listed on their account
    When User chooses to delete "MoonName"
    Then User no longer sees "MoonName" in their list of moons

  Scenario: Users receive an error when trying to delete a non-existing moon
    Given User does not have moon "NonExistentMoon" listed on their account
    When User tries to delete "NonExistentMoon"
    Then User gets an error saying "Moon not found"
