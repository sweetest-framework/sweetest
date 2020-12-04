package com.mysugr.android.testing.example.net

class UsernameOrPasswordWrongException : Exception()

class NotLoggedInException : Exception("User is not logged in (any more) on the backend")

class UserDoesNotExistException : Exception("User does not exist on the backend")

class UserAlreadyExistsException : Exception("User already exists on the backend")

class UnknownAuthTokenException : Exception("Unknown auth token")
