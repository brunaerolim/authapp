package com.example.authapp.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.authapp.ui.converter.HomeViewModel
import com.example.authapp.ui.converter.SignInViewModel
import com.example.authapp.ui.converter.SignUpViewModel
import com.example.authapp.ui.converter.toScreenState
import com.example.authapp.ui.screen.home.HomeScreen
import com.example.authapp.ui.screen.signin.SignInScreen
import com.example.authapp.ui.screen.signup.SignUpScreen

@Composable
fun AuthNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.SignIn.route
    ) {


        composable(Destinations.SignIn.route) {
            val viewModel: SignInViewModel = hiltViewModel()
            SignInScreen(
                state = viewModel.toScreenState(
                    onNavigateToSignUp = {
                        navController.navigate(Destinations.SignUp.route)
                    },
                    onGoogleSignIn = {
                        navController.navigate(Destinations.Home.route)
                    },
                    onNavigateToSignIn = {
                        navController.navigate(Destinations.Home.route)
                    }
                )
            )
        }
        composable(Destinations.SignUp.route) {
            val viewModel: SignUpViewModel = hiltViewModel()
            SignUpScreen(
                state = viewModel.toScreenState(
                    onNavigateToSignIn = {
                        navController.navigate(Destinations.SignIn.route)
                    }
                )
            )
        }
        composable(Destinations.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                state = viewModel.toScreenState(
                    onNavigateToSignIn = {
                        navController.navigate(Destinations.SignIn.route)
                    }
                )
            )
        }
    }
}