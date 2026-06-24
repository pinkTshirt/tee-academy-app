package com.teeacademy.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.teeacademy.app.core.designsystem.TeeAcademyTheme
import com.teeacademy.app.core.navigation.TeeAcademyNavRoot
import com.teeacademy.app.data.seed.SeedLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var seedLoader: SeedLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Seed on launch if needed (no-op fast path if already up to date —
        // see SeedLoader.seedIfNeeded). Runs before content is meaningfully
        // interactive, but does not block the splash/first frame.
        lifecycleScope.launch {
            seedLoader.seedIfNeeded()
        }

        setContent {
            TeeAcademyTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // Tablet breakpoint per UX spec Section 10: >= 840dp width.
                    val configuration = LocalConfiguration.current
                    val isWideScreen = configuration.screenWidthDp >= 840
                    TeeAcademyNavRoot(isWideScreen = isWideScreen)
                }
            }
        }
    }
}
