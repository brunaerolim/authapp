package com.example.authapp.presentation.screen.cardvalidation.failure

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.authapp.R

@Composable
fun PaymentFailureScreen(state: PaymentFailureState) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val displayMessage = state.errorMessage.ifBlank {
        state.errorThrowable?.localizedMessage ?: stringResource(R.string.payment_generic_error)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .semantics {
                contentDescription = "Payment failure screen"
            },
        color = MaterialTheme.colorScheme.background,
        onClick = {
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            val (errorIcon, titleText, messageText, tryAgainButton, backHomeButton) = createRefs()

            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .constrainAs(errorIcon) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(titleText.top, margin = 32.dp)
                        verticalChainWeight = 1f
                    },
                tint = MaterialTheme.colorScheme.error
            )

            Text(
                text = stringResource(R.string.payment_failure_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .constrainAs(titleText) {
                        top.linkTo(errorIcon.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(messageText.top, margin = 16.dp)
                        width = Dimension.fillToConstraints
                    }
                    .semantics {
                        contentDescription = "Title: $displayMessage"
                    }
            )

            Text(
                text = displayMessage,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .constrainAs(messageText) {
                        top.linkTo(titleText.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(tryAgainButton.top, margin = 48.dp)
                        width = Dimension.fillToConstraints
                    }
                    .semantics {
                        contentDescription = "Error message: $displayMessage"
                    }
            )

            Button(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    state.onTryAgain()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(tryAgainButton) {
                        top.linkTo(messageText.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(backHomeButton.top, margin = 16.dp)
                        width = Dimension.fillToConstraints
                    }
                    .semantics {
                        contentDescription = "Try again button"
                    }
            ) {
                Text(
                    text = stringResource(R.string.try_again),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            OutlinedButton(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    state.onBackToHome()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(backHomeButton) {
                        top.linkTo(tryAgainButton.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                        verticalChainWeight = 1f
                    }
                    .semantics {
                        contentDescription = "Back to home button"
                    }
            ) {
                Text(
                    text = stringResource(R.string.back_to_home),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}