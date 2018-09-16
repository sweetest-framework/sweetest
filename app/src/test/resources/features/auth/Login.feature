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

  Scenario: Non-existent user
    When trying to login or register with email address "unknown@test.com" and password "somepass"
    Then a new user with email "unknown@test.com" and password "somepass" is registered
    Then the user "unknown@test.com" is logged in as a new user

  Scenario: Password too short
    When trying to login or register with email address "unknown@test.com" and password "short"
    Then the user can't enter the app

  Scenario: Invalid email address
    When trying to login or register with email address "unknowntest.com"
    Then the user can't enter the app
