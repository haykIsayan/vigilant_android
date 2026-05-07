package com.example.project_vig_la

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_vig_la.di.DataModule
import com.example.project_vig_la.ui.theme.VigLaTheme
import com.example.project_vig_la.utils.LocationProviderImpl
import com.example.project_vig_la.vigilant.VigilantScreen
import com.example.project_vig_la.vigilant.state.MapViewModel
import com.example.project_vig_la.vigilant.state.MapViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VigLaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VigilantScreen(
                        innerPadding,
                        mapViewModel = viewModel() {
                            MapViewModelFactory(
                                repository = DataModule.crimeRepository,
                                locationProvider = LocationProviderImpl(
                                    this@MainActivity
                                )
                            ).create(MapViewModel::class.java)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VigLaTheme {
        Greeting("Android")
    }
}