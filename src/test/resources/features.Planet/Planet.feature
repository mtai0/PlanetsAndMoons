Feature:Planet Management

  Background:
    Given the user has an existing account
    And the user is logged in
    And the user is on the home page

  Scenario Outline: Add Planet - Valid
    Given the planet name <planetName> does not already exist
    And the Planet option is selected in the location select
    When the user enters <planetName> in the planet add input
    And clicks the submit planet button
    Then the planet name <planetName> should be added successfully to the Celestial Table

    Examples:
      | planetName   |
      | "earth"      |
      | "Jupiter"    |
      | "SATURN"     |
      | "  Venus  "  |
      | "MARS123"    |
      | "alpha@beta" |

  Scenario Outline: Add Planet - Invalid
    Given the Planet option is selected in the location select
    When the user enters <planetName> in the planet add input
    And clicks the submit planet button
    Then the Error alert should be displayed

    Examples: Negative Cases
      | planetName                                      |
      | ""                                              |
      | "AstroAdventureWonderlandWonderlandWonderland " |
      | "earth; DROP TABLE planets;"                    |

  Scenario: Add Planet - Planet Already Exists
    Given the planet name "earth" already exists
    And the Planet option is selected in the location select
    When the user enters "earth" in the planet add input
    And clicks the submit planet button
    Then  the Error alert should be displayed


  Scenario Outline: Remove Planet - Valid
    Given the planet name <planetName> already exists
    And the Planet option is selected in the location select
    When the user enters planet ID to delete <planetName> in the delete planet input
    And clicks the delete button
    Then the alert should be displayed for Planet <planetName> Deleted Successfully

    Examples:
      | planetName |
      | "earth"    |
      | "jupiter"  |

  Scenario Outline: Remove Planet - Invalid
    Given the planet name "saturn" already exists
    And the Planet option is selected in the location select
    When the user enters <planetName> in the delete planet input
    And clicks the delete button
    Then the Error alert should be displayed

    Examples:
      | planetName   |
      | "saturn"     |
      | ""           |
      | "1000000000" |

  Scenario Outline: Search Planet - Valid
    Given the planet name <planetName> already exists
    When the user enters <planetName> in the search planet input
    And clicks the search planet button
    Then the celestial table displays the <planetName>

    Examples:
      | planetName   |
      | "SATURN"     |
      | "  Venus  "  |
      | "MARS123"    |
      | "alpha@beta" |

  Scenario Outline: Search Planet - Invalid
    When the user enters <planetName> in the search planet input
    And clicks the search planet button
    Then the Error alert should be displayed

    Examples:
      | planetName                     |
      | "NonExistentPlanet"            |
      | "planet'; DROP TABLE planets;" |