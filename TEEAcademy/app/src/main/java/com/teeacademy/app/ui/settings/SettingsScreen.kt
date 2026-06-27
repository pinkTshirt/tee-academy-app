package com.teeacademy.app.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    // v1: theme follows system setting (see TeeAcademyTheme default param).
    // An explicit override switch is scaffolded here for when that's wanted;
    // wiring it to a persisted preference (DataStore) is a small v2 addition.
    var followSystemTheme by remember { mutableStateOf(true) }

    Scaffold(topBar = { TopAppBar(title = { Text("Settings") }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            androidx.compose.foundation.layout.Row(modifier = Modifier.fillMaxWidth()) {
                Text("Follow system dark mode", modifier = Modifier.padding(top = 12.dp))
                Switch(checked = followSystemTheme, onCheckedChange = { followSystemTheme = it })
            }
            Text(
                "TEE Academy v0.1.0",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 24.dp)
            )
            Text(
                "Content sourced per the app's References screen. " +
                    "This app does not display third-party advertising.",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
