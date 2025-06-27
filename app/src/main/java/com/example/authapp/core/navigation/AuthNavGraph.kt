package com.example.authapp.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.authapp.core.utils.Resource
import com.example.authapp.presentation.screen.cardvalidation.CardValidationScreen
import com.example.authapp.presentation.screen.cardvalidation.failure.PaymentFailureScreen
import com.example.authapp.presentation.screen.cardvalidation.success.PaymentSuccessScreen
import com.example.authapp.presentation.screen.home.HomeScreen
import com.example.authapp.presentation.screen.signin.SignInScreen
import com.example.authapp.presentation.screen.signin.forgot.ForgotPasswordScreen
import com.example.authapp.presentation.screen.signup.SignUpScreen
import com.example.authapp.presentation.viewmodel.cardvalidation.CardValidationViewModel
import com.example.authapp.presentation.viewmodel.cardvalidation.payment.failure.PaymentFailureViewModel
import com.example.authapp.presentation.viewmodel.cardvalidation.payment.success.paymentSuccessState
import com.example.authapp.presentation.viewmodel.cardvalidation.toScreenState
import com.example.authapp.presentation.viewmodel.home.HomeViewModel
import com.example.authapp.presentation.viewmodel.home.toScreenState
import com.example.authapp.presentation.viewmodel.signin.SignInViewModel
import com.example.authapp.presentation.viewmodel.signin.resetpassword.ForgotPasswordViewModel
import com.example.authapp.presentation.viewmodel.signin.resetpassword.toForgotScreenState
import com.example.authapp.presentation.viewmodel.signin.toScreenState
import com.example.authapp.presentation.viewmodel.signup.SignUpViewModel
import com.example.authapp.presentation.viewmodel.signup.toScreenState
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthNavGraph() {
    val navController = rememberNavController()

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
                        navController.navigate(Destinations.CARD_VALIDATION)
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

        composable(Destinations.CARD_VALIDATION) {
            val viewModel: CardValidationViewModel = koinViewModel()
            val state = viewModel.toScreenState(
                onBack = { navController.popBackStack() }
            )

            val validationResult = state.validationResult.value
            if (validationResult is Resource.Success) {
                viewModel.clearValidationResult()
                navController.navigate(Destinations.PAYMENT_SUCCESS)
            } else if (validationResult is Resource.Failure) {
                viewModel.clearValidationResult()
                navController.navigate("${Destinations.PAYMENT_FAILURE}/${validationResult}")
            }

            CardValidationScreen(state)
        }

        composable(Destinations.PAYMENT_SUCCESS) {
            PaymentSuccessScreen(
                paymentSuccessState(
                    onContinue = { navController.navigate(Destinations.HOME) { popUpTo(0) } },
                    onValidateAnother = {
                        navController.navigate(Destinations.CARD_VALIDATION) {
                            popUpTo(
                                Destinations.PAYMENT_SUCCESS
                            ) { inclusive = true }
                        }
                    }
                )
            )
        }

        composable(Destinations.PAYMENT_FAILURE) {
            val failureViewModel: PaymentFailureViewModel = koinViewModel()
            val state = failureViewModel.getState(
                onTryAgain = {
                    navController.navigate(Destinations.CARD_VALIDATION) {
                        popUpTo(Destinations.PAYMENT_FAILURE) { inclusive = true }
                    }
                },
                onBackToHome = {
                    navController.navigate(Destinations.HOME) { popUpTo(0) }
                }
            )
            PaymentFailureScreen(state)
        }
    }
}
