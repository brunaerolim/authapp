package com.example.authapp.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.authapp.data.User

@Composable
fun HomeScreen(
    state: HomeScreenState
) {

    HomeContent(
        user = state.user.value,
        isLoading = state.isLoading.value,
        onSignOut = state.onSignOut
    )
}

@Composable
fun HomeContent(
    user: User?,
    isLoading: Boolean,
    onSignOut: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Welcome!",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                user?.let {
                    Text(
                        text = "Hello, ${it.name ?: it.email}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = it.email!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                }

                Button(
                    onClick = onSignOut,
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Text("Sign Out")
                }
            }
        }
    }
}

@Preview
@Composable
fun HomePreview() {
    HomeScreen(
        state = HomeScreenState(
            user = remember { mutableStateOf(null) },
            isLoading = remember { mutableStateOf(false) },
            onSignOut = {}
        )
    )
}