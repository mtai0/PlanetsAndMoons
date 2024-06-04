Feature: Users can view moons in the Planetarium
  Background: User is logged in and on their dashboard.

  Scenario: Users can view all moons associated with their account
    Given User has moons associated with their account
    When User navigates to the view moons page
    Then User sees a list of all moons associated with their account

  Scenario: Users can view moons by planet
    Given User has multiple moons under different planets
    When User selects to view moons under "PlanetName"
    Then User sees all moons associated with "PlanetName"
