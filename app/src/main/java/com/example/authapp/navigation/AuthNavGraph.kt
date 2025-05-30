package com.example.authapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

            // Observa eventos de sucesso de login
            LaunchedEffect(Unit) {
                viewModel.signInSuccess.collect {
                    navController.navigate(Destinations.Home.route) {
                        popUpTo(Destinations.SignIn.route) { inclusive = true }
                    }
                }
            }

            SignInScreen(
                state = viewModel.toScreenState(
                    onNavigateToSignUp = {
                        navController.navigate(Destinations.SignUp.route)
                    },
                    onGoogleSignIn = {},
                )
            )
        }

        composable(Destinations.SignUp.route) {
            val viewModel: SignUpViewModel = hiltViewModel()

            LaunchedEffect(Unit) {
                viewModel.signUpSuccess.collect {
                    navController.navigate(Destinations.Home.route) {
                        popUpTo(Destinations.SignUp.route) { inclusive = true }
                    }
                }
            }

            SignUpScreen(
                state = viewModel.toScreenState(
                    onNavigateToSignIn = {
                        navController.popBackStack()
                    }
                )
            )
        }

        composable(Destinations.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                state = viewModel.toScreenState(
                    onNavigateToSignIn = {
                        navController.navigate(Destinations.SignIn.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            )
        }
    }
}