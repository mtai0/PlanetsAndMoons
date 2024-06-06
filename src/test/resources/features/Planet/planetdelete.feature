Feature: Users can manage PLants in planetarium
  Background: User is logged in and on the Planet ManageMent Page
    Given User is on the Planet Management page

    Scenario Outline: Users can delete an existing planet
      Given a planet with the name "<planetName>" already exists
      When  User selects  planet "<planetName>'
      And  User clicks on the delete planet button
      Then planet deletion is successful

      Examples:
      |planetName|
      |Earth     |
      |Mars      |


      Scenario Outline: Users cannot delete a planet if it doesnt exist
        Given no planet exists with the name "<planetName>"
        When  User selects the planet "<planetName>"
        And User clicks on the delete planet button
        Then Nothing happens

        Examples:
        |planetName|
        |Venus     |
        |Saturn    |