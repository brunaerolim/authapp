package com.example.authapp.core.navigation

sealed class Destinations(val route: String) {
    object SignIn : Destinations("sign_in")
    object SignUp : Destinations("sign_up")
    object Home : Destinations("home")
}