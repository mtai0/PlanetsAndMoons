Feature: Users can manage planets in the Planetarium
  Background: User is logged in and on the Planet Management page
    Given User is on the Planet Management page

  Scenario Outline: Users can create a new planet
    Given no planet exists with "<planetName>" name
    And "<planetName>" is of valid length
    When User inputs the planet details "<planetName>"
    And User clicks on the add planet button
    Then planet creation is successful
    Examples:
      |planetName |
      | Earth      |
      | Mars       |

  Scenario Outline: Users cannot create a planet if the planet name length is invalid
    Given no planet "<planetName>" exists
    And "<planetName>" is of invalid length
    When User inputs the moon details  "<planetName>"
    And User clicks on the add planet button to
    Then planet creation fails
    Examples:
      | planetName |
      | Earth      |
      | Mars       |

  Scenario Outline: Users cannot create a planet if there is already a planet with the given name
    Given a planet with the name "<planetName>"  exists
    When User inputs the moon details "<moonName>" | "<planetName>"
    And User clicks on the add planet button
    Then planet creation fails
    Examples:
      | planetName |
      | Earth      |
      | Mars       |
