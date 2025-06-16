package com.example.authapp.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.authapp.presentation.screen.home.HomeScreen
import com.example.authapp.presentation.screen.signin.SignInScreen
import com.example.authapp.presentation.screen.signin.forgot.ForgotPasswordScreen
import com.example.authapp.presentation.screen.signup.SignUpScreen
import com.example.authapp.presentation.viewmodel.home.HomeViewModel
import com.example.authapp.presentation.viewmodel.home.toScreenState
import com.example.authapp.presentation.viewmodel.signin.SignInViewModel
import com.example.authapp.presentation.viewmodel.signin.resetpassword.ForgotPasswordViewModel
import com.example.authapp.presentation.viewmodel.signin.resetpassword.toForgotScreenState
import com.example.authapp.presentation.viewmodel.signin.toScreenState
import com.example.authapp.presentation.viewmodel.signup.SignUpViewModel
import com.example.authapp.presentation.viewmodel.signup.toScreenState

@Composable
fun AuthNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.SIGN_IN
    ) {
        composable(Destinations.SIGN_IN) {
            val viewModel: SignInViewModel = hiltViewModel()

            LaunchedEffect(Unit) {
                viewModel.signInSuccess.collect {
                    navController.navigate(Destinations.HOME) {
                        popUpTo(Destinations.SIGN_IN) { inclusive = true }
                    }
                }
            }

            SignInScreen(
                state = viewModel.toScreenState(
                    onNavigateToSignUp = {
                        navController.navigate(Destinations.SIGN_UP)
                    },
                    onNavigateToForgotPassword = {
                        navController.navigate(Destinations.FORGOT_PASSWORD)
                    }
                )
            )
        }

        composable(Destinations.SIGN_UP) {
            val viewModel: SignUpViewModel = hiltViewModel()

            LaunchedEffect(Unit) {
                viewModel.signUpSuccess.collect {
                    navController.navigate(Destinations.HOME) {
                        popUpTo(Destinations.SIGN_UP) { inclusive = true }
                    }
                }
            }

            SignUpScreen(
                state = viewModel.toScreenState(
                    onNavigateToSignIn = {
                        navController.popBackStack()
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            )
        }

        composable(Destinations.HOME) {
            val viewModel: HomeViewModel = hiltViewModel()

            HomeScreen(
                state = viewModel.toScreenState(
                    onNavigateToSignIn = {
                        navController.navigate(Destinations.SIGN_IN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onSignOut = {
                        navController.navigate(Destinations.SIGN_IN) {
                            popUpTo(Destinations.SIGN_IN) { inclusive = true }
                        }
                    }
                )
            )
        }

        composable(Destinations.FORGOT_PASSWORD) {
            val viewModel: ForgotPasswordViewModel = hiltViewModel()
            val state = viewModel.toForgotScreenState(
                onNavigateToResetPassword = { email ->
                    navController.navigate("${Destinations.SIGN_IN}?email=$email")
                }
            )

            LaunchedEffect(Unit) {
                viewModel.navigateToBack.collect {
                    navController.popBackStack()
                }
            }

            ForgotPasswordScreen(state = state)
        }
    }
}