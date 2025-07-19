package com.example.authapp.core.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
import io.sentry.Sentry
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AuthNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()

    LaunchedEffect(navBackStackEntry.value?.destination?.route) {
        navBackStackEntry.value?.destination?.route?.let { route ->
            Sentry.addBreadcrumb("Navigated to $route")
        }
    }

    NavHost(
        navController = navController,
        startDestination = Destinations.SIGN_IN
    ) {
        composable(Destinations.SIGN_IN) {
            val viewModel: SignInViewModel = koinViewModel()

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
            val viewModel: SignUpViewModel = koinViewModel()

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
            val viewModel: HomeViewModel = koinViewModel()
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
                    },
                    onNavigateToCardValidation = {
                        navController.navigate(Destinations.PAYMENT)
                    }
                )
            )
        }

        composable(Destinations.FORGOT_PASSWORD) {
            val viewModel: ForgotPasswordViewModel = koinViewModel()
            val state = viewModel.toForgotScreenState(
                onNavigateToResetPassword = { email ->
                    navController.navigate("${Destinations.SIGN_IN}?email=$email")
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )

            ForgotPasswordScreen(state = state)
        }
    }

}
