Feature: Planet Creation

  Background: user is on planet Management page
    Given user is logged in and on the planet management page

  Scenario Outline: Add Planet - Valid
    Given the planet name "<planetName>" does not already exist
    And "<planetName>" is of valid length and not empty
    When the user enters "<planetName>" in the planet add input
    And clicks the submit planet button
    Then the planet name "<planetName>" is added successfully to the Celestial Table

    Examples:
      | planetName   |
      | "earth"      |
      | "Jupiter"    |
      | "SATURN"     |
      | "MARS123"    |
      | "alpha@beta" |

  Scenario Outline: Add Planet - Invalid
    Given no planet exists where planet name "<planetName>"
    And "<planetName>" is of invalid length and is empty
    When  User inputs planet details "<planetName>"
    And User clicks on planet Submit button
    Then  creation fails for planet

    Examples: Negative Cases
      | planetName                                      |
      | ""                                              |
      | "AstroAdventureWonderlandWonderlandWonderland " |
      | "213443"                                        |

  Scenario Outline: Users cant create a planet that already Exists
    Given a planet with name "<planetName>" already exists
    When User inputs the planet details "<planetName>"
    And User clicks on the add planet button
    Then Planet Creation Fails
    Examples:
      |planetName|
      |"earth"          |
      |"Jupiter"|
      |"SATURN" |