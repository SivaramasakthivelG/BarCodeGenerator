package com.example.barcodegenerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.barcodegenerator.navigation.NavigationScreen
import com.example.barcodegenerator.ui.theme.BarCodeGeneratorTheme
import com.example.barcodegenerator.vm.SharedViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = ViewModelProvider(this)[SharedViewModel::class.java]
            BarCodeGeneratorTheme {
                NavigationScreen(viewModel)
                
            }
        }
    }
}





