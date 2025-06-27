package com.example.authapp.presentation.screen.cardvalidation.success

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.authapp.R

@Composable
fun PaymentSuccessScreen(state: PaymentSuccessState) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .semantics(mergeDescendants = true) {},
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
            val (successIcon, titleText, messageText, continueButton, validateButton) = createRefs()

            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = stringResource(R.string.payment_success_icon_description),
                modifier = Modifier
                    .size(120.dp)
                    .constrainAs(successIcon) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(titleText.top, margin = 32.dp)
                        verticalChainWeight = 1f
                    },
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stringResource(R.string.payment_success_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .constrainAs(titleText) {
                        top.linkTo(successIcon.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(messageText.top, margin = 16.dp)
                        width = Dimension.fillToConstraints
                    }
                    .semantics {}
            )

            Text(
                text = stringResource(R.string.payment_success_message),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .constrainAs(messageText) {
                        top.linkTo(titleText.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(continueButton.top, margin = 48.dp)
                        width = Dimension.fillToConstraints
                    }
                    .semantics {}
            )

            Button(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    state.onContinue()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(continueButton) {
                        top.linkTo(messageText.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(validateButton.top, margin = 16.dp)
                        width = Dimension.fillToConstraints
                    }
                    .semantics {}
            ) {
                Text(
                    text = stringResource(R.string.continue_to_home),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            OutlinedButton(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    state.onValidateAnother()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(validateButton) {
                        top.linkTo(continueButton.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                        verticalChainWeight = 1f
                    }
                    .semantics {}
            ) {
                Text(
                    text = stringResource(R.string.validate_another_card),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}