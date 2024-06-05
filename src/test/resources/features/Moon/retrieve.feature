Feature: Users can view moons in the Planetarium
  Background: User is logged in and on the Moon Management page
   Given User is on the Moon Management page


  Scenario Outline: Users can view moons by specific planet
    Given User has multiple moons under a planet "<MoonName>"
    When User selects to search moon "<MoonName>"
    Then User sees moon row with "<MoonName>"
    Examples:
      | MoonName |
      | Photo    |
      | Luna     |
