package com.example.barcodegenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.barcodegenerator.navigation.NavigationScreen
import com.example.barcodegenerator.ui.theme.BarCodeGeneratorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BarCodeGeneratorTheme {
                NavigationScreen()
            }
        }
    }
}





