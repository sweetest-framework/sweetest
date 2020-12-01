@login-integration

Feature: Login, registration and logout

  Background:
    Given there is already a user at the backend with email address "test@test.com" and password "secure1"

  Scenario: User exists
    When trying to login or register with email address "test@test.com" and password "secure1"
    Then the user "test@test.com" is logged in as an existing user

  Scenario: Existing user, incorrect password
    When trying to login or register with email address "test@test.com" and password "wrongpass"
    Then the user can't enter the app

  Scenario: New user
    When trying to login or register with email address "new@test.com" and password "secure2"
    Then there is a user at the backend with email address "new@test.com" and password "secure2"
    And the user "new@test.com" is logged in as a new user

  Scenario Outline: Invalid email address
    When trying to login or register with email address "<email>" and password "something"
    Then the user can't enter the app
    And a wrong email address is detected
    Examples:
      | email       |
      | ajehf@.com  |
      | wrong.net   |
      | @sth.org    |
