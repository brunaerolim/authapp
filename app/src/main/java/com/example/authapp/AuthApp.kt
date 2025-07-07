package com.example.authapp

import android.app.Application
import com.example.authapp.core.di.appModule
import io.sentry.SentryLevel
import io.sentry.android.core.SentryAndroid
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AuthApp : Application() {

    override fun onCreate() {
        super.onCreate()

        initializeSentry()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@AuthApp)
            modules(appModule)
        }
    }

    private fun initializeSentry() {
        SentryAndroid.init(this) { options ->
            options.dsn = BuildConfig.SENTRY_KEY

            options.environment = if (BuildConfig.DEBUG) "development" else "production"
            options.isDebug = BuildConfig.DEBUG_MODE

            options.setBeforeSend { event, _ ->
                if (BuildConfig.DEBUG && event.level == SentryLevel.DEBUG) {
                    null
                } else {
                    event
                }
            }

            options.release = "${BuildConfig.APPLICATION_ID}@${BuildConfig.VERSION_NAME}"

            options.setTag("app.version", BuildConfig.VERSION_NAME)
            options.setTag("app.build", BuildConfig.VERSION_CODE.toString())
        }
    }
}