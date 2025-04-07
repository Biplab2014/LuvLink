package com.luv.link

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.luv.link.ui.onBoard.OnBoardActivity
import kotlinx.coroutines.delay
import com.luv.link.ui.theme.DigiSignTheme
import com.luv.link.viewModels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DigiSignTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    SplashScreen()
                }
            }
        }

        // Use this user model to load data
        userViewModel.user.observe(this) { user ->
            // Log.d("MainActivity", "User: $user")
        }

        userViewModel.loadUser(1)
    }
}

@Composable
fun SplashScreen() {
    var isSplashVisible by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(5000) // Show splash for 5 seconds
        isSplashVisible = false
        // Navigate to OnBoardActivity
        // You can use an Intent to start the OnBoardActivity
        // This part will be handled in the MainActivity

        context.startActivity(Intent(context, OnBoardActivity::class.java))
    }

    if (isSplashVisible) {
        SplashContent()
    }
}

@Composable
fun SplashContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF007BFF)), // Blue background
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.app_name),
            color = Color.White,
            fontSize = 32.sp // Adjust font size as needed
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMqttScreen() {
    DigiSignTheme {
        SplashContent()
    }
}
