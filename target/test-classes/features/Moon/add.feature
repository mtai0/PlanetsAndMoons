Feature: Users can manage moons in the Planetarium
  Background: User is logged in and on the Moon Management page
    Given User is on the Moon Management page

  Scenario Outline: Users can create a new moon
    Given no moon exists with the name "<moonName>" on planet "<planetName>"
    And "<moonName>" is of valid length
    When User inputs the moon details "<moonName>" | "<planetName>"
    And User clicks on the add moon button
    Then moon creation is successful
    Examples:
      | moonName | planetName |
      | Luna     | Earth      |
      | Phobos   | Mars       |

  Scenario Outline: Users cannot create a moon if the moon name length is invalid
    Given no moon exists with the name "<moonName>" on planet "<planetName>"
    And "<moonName>" is of invalid length
    When User inputs the moon details "<moonName>" | "<planetName>"
    And User clicks on the add moon button
    Then moon creation fails
    Examples:
      | moonName                                       | planetName |
      | ExtremelyLongMoonNameThatExceedsExpectedLength | Earth      |
      |                                                | Mars       |

  Scenario Outline: Users cannot create a moon if there is already a moon with the given name on the same planet
    Given a moon with the name "<moonName>" already exists on planet "<planetName>"
    When User inputs the moon details "<moonName>" | "<planetName>"
    And User clicks on the add moon button
    Then moon creation fails
    Examples:
      | moonName | planetName |
      | Luna     | Earth      |
      | Phobos   | Mars       |
