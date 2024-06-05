Feature: Users can manage moons in the Planetarium
  Background: User is logged in and on the Moon Management page
    Given User is on the Moon Management page

  Scenario Outline: Users can delete an existing moon
    Given a moon with the name "<moonName>" already exists on planet "<planetName>"
    When User selects the moon "<moonName>" | "<planetName>"
    And User clicks on the delete moon button
    Then moon deletion is successful
    Examples:
      | moonName | planetName |
      | Luna     | Earth      |
      | Phobos   | Mars       |

  Scenario Outline: Users cannot delete a moon if it does not exist
    Given no moon exists with the name "<moonName>" on planet "<planetName>"
    When User selects the moon "<moonName>" | "<planetName>"
    And User clicks on the delete moon button
    Then Nothing Happens
    Examples:
      | moonName | planetName |
      | Deimos   | Mars       |
      | Titan    | Earth     |
