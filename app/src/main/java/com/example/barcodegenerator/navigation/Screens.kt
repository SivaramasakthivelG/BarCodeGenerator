package com.example.barcodegenerator.navigation

sealed class Screens(val route: String){
    object TextToBarcode: Screens("textToBarcode")
    object ImageToBarcode: Screens("imageToBarcode")
}
