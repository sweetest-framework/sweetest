@login-integration

Feature: Login, registration and logout

  Background:
    Given there is a user existing with email address "test@test.com" and password "secure1"

  Scenario: User exists
    When trying to login or register with email address "test@test.com" and password "secure1"
    Then the user "test@test.com" is logged in as an existing user

  Scenario: Existing user, incorrect password
    When trying to login or register with email address "test@test.com" and password "wrongpass"
    Then the user can't enter the app

  Scenario Outline: Invalid email address
    When trying to login or register with email address "<email>"
    Then the user can't enter the app
    Examples:
      | email       |
      | ajehf@.com  |
      | wrong.net   |
      | @sth.org    |
