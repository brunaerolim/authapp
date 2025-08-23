package com.example.authapp.core.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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

    val homeViewModel: HomeViewModel = koinViewModel()
    val currentUser by homeViewModel.currentUser.collectAsState()
    val userPreferences by homeViewModel.userPreferences.collectAsState()

    val startDestination = if (currentUser != null && userPreferences.isLoggedIn) {
        Screen.Home.route
    } else {
        Screen.SignIn.route
    }

    LaunchedEffect(navBackStackEntry.value?.destination?.route) {
        navBackStackEntry.value?.destination?.route?.let { route ->
            Sentry.addBreadcrumb("Navigated to $route")
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.SignIn.route) {
            val viewModel: SignInViewModel = koinViewModel()

            LaunchedEffect(Unit) {
                viewModel.signInSuccess.collect {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            SignInScreen(
                state = viewModel.toScreenState(
                    onNavigateToSignUp = {
                        navController.navigate(Screen.SignUp.route)
                    },
                    onNavigateToForgotPassword = {
                        navController.navigate(Screen.ForgotPassword.route)
                    }
                )
            )
        }

        composable(
            route = Screen.SignInWithEmail.route,
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val viewModel: SignInViewModel = koinViewModel()
            val email = backStackEntry.arguments?.getString("email") ?: ""

            LaunchedEffect(email) {
                if (email.isNotEmpty()) {
                    viewModel.onEmailChange(email)
                }
            }

            LaunchedEffect(Unit) {
                viewModel.signInSuccess.collect {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            SignInScreen(
                state = viewModel.toScreenState(
                    onNavigateToSignUp = {
                        navController.navigate(Screen.SignUp.route)
                    },
                    onNavigateToForgotPassword = {
                        navController.navigate(Screen.ForgotPassword.route)
                    }
                )
            )
        }

        composable(Screen.SignUp.route) {
            val viewModel: SignUpViewModel = koinViewModel()

            LaunchedEffect(Unit) {
                viewModel.signUpSuccess.collect {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            SignUpScreen(
                state = viewModel.toScreenState(
                    onNavigateToSignIn = {
                        navController.navigate(Screen.SignIn.route) {
                            popUpTo(Screen.SignUp.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = {
                        navController.navigate(Screen.SignIn.route) {
                            popUpTo(Screen.SignUp.route) { inclusive = true }
                        }
                    }
                )
            )
        }

        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = koinViewModel()

            LaunchedEffect(Unit) {
                viewModel.signOutSuccess.collect {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            HomeScreen(
                state = viewModel.toScreenState(
                    onNavigateToSignIn = {
                        navController.navigate(Screen.SignIn.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onSignOut = {
                    },
                    onNavigateToCardValidation = {
                        navController.navigate(Screen.Payment.route)
                    }
                )
            )
        }

        // Tela Forgot Password
        composable(Screen.ForgotPassword.route) {
            val viewModel: ForgotPasswordViewModel = koinViewModel()
            val state = viewModel.toForgotScreenState(
                onNavigateToResetPassword = { email ->
                    navController.navigate(Screen.SignInWithEmail.createRoute(email)) {
                        popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )

            ForgotPasswordScreen(state = state)
        }
    }
}