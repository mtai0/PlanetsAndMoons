Feature: Users can create an account for the Planetarium.
  Background: User is on the Create Account page
    Given User is on the Create Account page

  Scenario Outline: Users can create an account.
    Given no account exists with the username "<username>"
    And both "<username>" and "<password>" are of valid length (0 < Length <= 30)
    When User inputs the given account credentials "<username>" | "<password>"
    And User clicks on the registration button
    Then account creation is successful
    Examples:
      | username | password |
      | "username" | "password" |
      | "test"     | "12345"    |

  Scenario Outline: Users cannot create an account if password length is invalid.
    Given no account exists with the username "<username>"
    And "<password>" is of invalid length (Length = 0 || Length > 30)
    When User inputs the given account "<username>" | "<password>"
    And User clicks on the registration button
    Then account creation fails
    Examples:
      | username | password |
      | "username" | "moreThan30Characters00000000000" |
      | "username" | "" |

  Scenario Outline: Users cannot create an account if username length is invalid.
    Given no account exists with the username "<username>"
    And "<username>" is of invalid length (Length = 0 || Length > 30)
    When User inputs the given account "<username>" | "<password>"
    And User clicks on the registration button
    Then account creation fails
    Examples:
      | username | password |
      | "moreThan30Characters00000000000" | "password" |
      | "" | "password" |

  Scenario Outline: Users cannot create an account if there is already an account with the given username.
    Given an account with the username "<username>" already exists
    And both "<username>" and "<password>" are of valid length (0 < Length <= 30)
    When User inputs the given account "<username>" | "<password>"
    And User clicks on the registration button
    Then account creation fails
    Examples:
      | username | password |
      | "existingUser" | "password" |