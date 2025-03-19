package com.luv.link

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luv.link.ui.theme.DigiSignTheme
import com.luv.link.viewModels.UserViewModel
import com.luv.link.viewModels.mqtt.MqttViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DigiSignTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MqttScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        // use this user model to load data
        userViewModel.user.observe(this) { user ->
            // Log.d("MainActivity", "User: $user")
        }

        userViewModel.loadUser(1)
    }
}

@Composable
fun MqttScreen(
    modifier: Modifier = Modifier,
    mqttViewModel: MqttViewModel = viewModel()
) {
    var inputMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = inputMessage,
            onValueChange = { inputMessage = it },
            label = { Text("Enter Message") }
        )

        Button(onClick = {
            mqttViewModel.publishMessage("topic", inputMessage)
        }) {
            Text("Publish")
        }

        Text(text = "Last Received Message: ")
        mqttViewModel.mqttMessage.observeForever { message ->
            // Log.d("MQTT", "Received: $message")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMqttScreen() {
    DigiSignTheme {
        MqttScreen()
    }
}
