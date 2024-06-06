Feature: Users can view Planets in the Planetarium
  Background:  User is logged in and on the dashboard
    Given User is on the Planet Management page

    Scenario Outline: User can view planets by specific name
      Given the planetname "<PlanetName>" exists
      When User selects to search for planet "<PlanetName>" and inputs it in as input
      Then User sees planet row with "<PlanetName>"

      Examples:
      |PlanetName|
      | Earth    |
      | Mars        |