package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainApp
import com.example.ui.components.LocalIsTvDevice
import com.example.ui.components.isTvDevice
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.RecipeViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    val splashScreen = installSplashScreen()
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val context = LocalContext.current
      val isTv = remember(context) { context.isTvDevice() }
      val isSystemDark = isSystemInDarkTheme()
      val useDarkTheme = isTv || isSystemDark

      var showSplash by rememberSaveable { mutableStateOf(true) }

      CompositionLocalProvider(LocalIsTvDevice provides isTv) {
        MyApplicationTheme(darkTheme = useDarkTheme) {
          Surface(modifier = Modifier.fillMaxSize()) {
            Crossfade(
              targetState = showSplash,
              animationSpec = tween(durationMillis = 400),
              label = "splashAppCrossfade"
            ) { isSplash ->
              if (isSplash) {
                SplashScreen(
                  onSplashFinished = { showSplash = false }
                )
              } else {
                val recipeViewModel: RecipeViewModel = viewModel()
                MainApp(viewModel = recipeViewModel)
              }
            }
          }
        }
      }
    }
  }
}


