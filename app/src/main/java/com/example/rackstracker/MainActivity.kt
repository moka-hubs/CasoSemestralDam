package com.example.rackstracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.rackstracker.ui.theme.Purple80
import com.example.rackstracker.ui.theme.RacksTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RacksTrackerTheme {
                val nombre = "Moka"
                var numero by remember { mutableIntStateOf(0) }
                fun aumentar (){
                    numero++
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Hola Soy $nombre",
                            modifier = Modifier.background(color = Color.Yellow)

                        )
                        Text(
                            text = "Hola Dahyun",
                            modifier = Modifier.background(color = Color.Blue)

                        )
                        Text(
                            text = "Como estas $nombre?",
                            modifier = Modifier.background(color = Color.Yellow)
                        )
                        Text(
                            text = "Bien 🆗",
                            modifier = Modifier.background(color = Purple80)
                        )
                        Button(onClick = {}) {
                            Text(text = "Seleccionar")
                        }

                    }
                }
            }
        }
    }
}

