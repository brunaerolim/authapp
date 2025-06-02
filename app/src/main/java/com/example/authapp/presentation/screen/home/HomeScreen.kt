package com.example.authapp.presentation.screen.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.authapp.domain.model.User
import com.example.authapp.presentation.theme.PastelPink
import com.example.authapp.presentation.theme.PastelPinkDark

@Composable
fun HomeScreen(
    state: HomeScreenState
) {
    HomeContent(
        currentUser = state.currentUser.value,
        userPreferences = state.userPreferences.value,
        isLoading = state.isLoading.value,
        showSignOutDialog = state.showSignOutDialog.value,
        onSignOut = state.onSignOut,
        onShowSignOutDialog = state.onShowSignOutDialog,
        onHideSignOutDialog = state.onHideSignOutDialog,
        onRefreshUserData = state.onRefreshUserData
    )
}

@Composable
fun HomeContent(
    currentUser: User?,
    userPreferences: com.example.authapp.data.local.UserPreferences,
    isLoading: Boolean,
    showSignOutDialog: Boolean,
    onSignOut: () -> Unit,
    onShowSignOutDialog: () -> Unit,
    onHideSignOutDialog: () -> Unit,
    onRefreshUserData: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = PastelPink)
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // Profile Image
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(currentUser?.photoUrl ?: userPreferences.userPhotoUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(3.dp, PastelPink, CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Welcome back!",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = PastelPinkDark,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = currentUser?.name ?: userPreferences.userName,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = PastelPink,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Text(
                            text = currentUser?.email ?: userPreferences.userEmail,
                            style = MaterialTheme.typography.bodyMedium,
                            color = PastelPinkDark.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 32.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onRefreshUserData,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = PastelPink
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Refresh")
                            }

                            Button(
                                onClick = onShowSignOutDialog,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PastelPink,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sign Out")
                            }
                        }
                    }
                }
            }
        }

        // Sign Out Confirmation Dialog
        if (showSignOutDialog) {
            AlertDialog(
                onDismissRequest = onHideSignOutDialog,
                title = {
                    Text(
                        text = "Sign Out",
                        color = PastelPinkDark,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to sign out?",
                        color = PastelPinkDark.copy(alpha = 0.8f)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = onSignOut,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PastelPink,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Sign Out")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = onHideSignOutDialog,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = PastelPinkDark
                        )
                    ) {
                        Text("Cancel")
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}