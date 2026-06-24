package com.teeacademy.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point. Hilt's component tree is rooted here.
 * Seed loading is triggered from MainActivity's first composition via
 * SettingsViewModel/AppStartupUseCase rather than here, to keep this
 * class free of work that should be testable/observable from the UI layer.
 */
@HiltAndroidApp
class TeeAcademyApp : Application()
