package com.example.authapp.core.navigation

object Destinations {
    const val SIGN_IN = "sign_in"
    const val SIGN_UP = "sign_up"
    const val HOME = "home"
    const val FORGOT_PASSWORD = "forgot_password"
    const val PAYMENT = "payment"
}

sealed class Screen(val route: String) {
    object SignIn : Screen("sign_in")
    object SignUp : Screen("sign_up")
    object Home : Screen("home")
    object ForgotPassword : Screen("forgot_password")
    object Payment : Screen("payment")

    object SignInWithEmail : Screen("sign_in/{email}") {
        fun createRoute(email: String) = "sign_in/$email"
    }
}