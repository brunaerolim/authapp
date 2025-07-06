package com.example.authapp.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.authapp.core.resources.ResourceProvider
import com.example.authapp.core.resources.ResourceProviderImpl
import com.example.authapp.data.local.UserPreferencesDataStore
import com.example.authapp.data.remote.FireBaseAuthDataSource
import com.example.authapp.data.repository.auth.AuthRepository
import com.example.authapp.data.repository.auth.AuthRepositoryImpl
import com.example.authapp.presentation.viewmodel.home.HomeViewModel
import com.example.authapp.presentation.viewmodel.signin.SignInViewModel
import com.example.authapp.presentation.viewmodel.signin.resetpassword.ForgotPasswordViewModel
import com.example.authapp.presentation.viewmodel.signup.SignUpViewModel
import com.google.firebase.auth.FirebaseAuth
import com.stripe.android.Stripe
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

val appModule = module {

    // Firebase Auth
    single<FirebaseAuth> { FirebaseAuth.getInstance() }

    single<ResourceProvider> { ResourceProviderImpl(androidContext()) }

    // DataStore
    single<DataStore<Preferences>> { androidContext().dataStore }

    // Data Sources
    single { FireBaseAuthDataSource(get()) }
    single { UserPreferencesDataStore(get()) }

    // Repositories
    single<AuthRepository> {
        AuthRepositoryImpl(
            auth = get(),
            dataSource = get()
        )
    }

    // Stripe
    single<Stripe> {
        Stripe(androidContext(), "pk_test_your_publishable_key_here")
    }

    // ViewModels
    viewModel { SignInViewModel(get(), get()) }
    viewModel { SignUpViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { ForgotPasswordViewModel(get()) }
}