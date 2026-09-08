package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.data.AppLanguage
import com.example.ui.MainScreen
import com.example.ui.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme(darkTheme = true) {
        SuzukiDevApp()
      }
    }
  }
}

@Composable
fun SuzukiDevApp() {
  var showSplash by remember { mutableStateOf(true) }
  
  // Detect default system locale or default to Spanish
  val initialLang = remember {
    when (Locale.getDefault().language.lowercase()) {
      "es" -> AppLanguage.ES
      "en" -> AppLanguage.EN
      "pt" -> AppLanguage.PT
      "fr" -> AppLanguage.FR
      "de" -> AppLanguage.DE
      "zh" -> AppLanguage.ZH
      "ja" -> AppLanguage.JA
      else -> AppLanguage.ES
    }
  }
  var currentLanguage by remember { mutableStateOf(initialLang) }

  Crossfade(
    targetState = showSplash,
    animationSpec = tween(durationMillis = 400),
    label = "splash_crossfade"
  ) { isSplash ->
    if (isSplash) {
      SplashScreen(
        language = currentLanguage,
        onFinished = { showSplash = false }
      )
    } else {
      MainScreen(
        currentLanguage = currentLanguage,
        onLanguageChange = { currentLanguage = it }
      )
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme { Greeting("Android") }
}

