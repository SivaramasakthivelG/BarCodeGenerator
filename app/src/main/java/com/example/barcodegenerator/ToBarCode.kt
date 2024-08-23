package com.example.barcodegenerator

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.barcodegenerator.vm.SharedViewModel

@Composable
fun ToBarcode(modifier: Modifier, viewModel: SharedViewModel) {

    val bitmap: Bitmap? by viewModel.bitmap.observeAsState()
    val text: String? by viewModel.text.observeAsState()

    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ){
        Column {
            bitmap?.let {
                Image(bitmap = bitmap!!.asImageBitmap(), contentDescription = "BitmapQR")
            }

            Spacer(modifier = modifier.height(20.dp))

            Text(text = text.toString())
        }


    }


}
