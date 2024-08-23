package com.example.barcodegenerator.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.barcodegenerator.GenerateBarcode
import com.example.barcodegenerator.ToBarcode
import com.example.barcodegenerator.vm.SharedViewModel


@Composable
fun NavigationScreen(viewModel: SharedViewModel) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screens.TextToBarcode.route) {
        composable(Screens.TextToBarcode.route) {
            GenerateBarcode(modifier = Modifier, navController,viewModel)
        }
        composable(
            Screens.ToBarCode.route
        ){
            ToBarcode(modifier = Modifier,viewModel)
        }
    }


}