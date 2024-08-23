package com.example.barcodegenerator

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import android.graphics.Paint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.barcodegenerator.navigation.Screens
import com.example.barcodegenerator.vm.SharedViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognition.*
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.InputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GenerateBarcode(modifier: Modifier, navController: NavController, viewModel: SharedViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val barCodeGenerate = remember { mutableStateOf("") }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val contentResolver = context.contentResolver

    var extractedText by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        selectedImageUri = it
        it?.let {
            val bitmap = getBitmapFromUri(uri = it, contentResolver)

            coroutineScope.launch {
                extractedText = extractTextFromBitmap(bitmap)
            }
            viewModel.setText(extractedText)
        }
    }


    Surface(color = MaterialTheme.colorScheme.primary) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Generate Bar Code",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                )
            }
        ) {
            /*Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                bitmap.value?.asImageBitmap()?.let { it ->
                    Image(
                        bitmap = it,
                        contentDescription = "Generate BarCode Image",
                        modifier = Modifier.size(250.dp),
                    )
                }
            }*/

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp, 15.dp, 15.dp, 0.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                OutlinedTextField(
                    value = barCodeGenerate.value,
                    onValueChange = { barCodeGenerate.value = it },
                    label = { Text(text = "Enter BarCode") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                Spacer(modifier = Modifier.height(25.dp))


                Button(
                    onClick = {
                        if (barCodeGenerate.value.isEmpty()) {
                            bitmap.value = createSimpleBitmap()
                            viewModel.setBitmap(bitmap.value!!)
                        } else {
                            bitmap.value = generateBarCode(barCodeGenerate.value)
                            viewModel.setBitmap(bitmap.value!!)
                        }
                        navController.navigate(Screens.ToBarCode.route)

                    },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(60.dp)
                        .padding(10.dp),
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(Gray)
                ) {
                    Text(
                        text = "Submit",
                        color = androidx.compose.ui.graphics.Color.White,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier.height(40.dp))

                Button(
                    onClick = {
                        launcher.launch("image/*")
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(60.dp)
                        .padding(10.dp),
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(Gray)
                )
                {
                    Text(
                        text = "Pick Image",
                        color = White,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier.height(40.dp))

                Button(
                    onClick = {
                        navController.navigate(Screens.ToBarCode.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(60.dp)
                        .padding(10.dp),
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(Gray)
                ) {
                    Text(
                        text = "LoadText",
                        color = White,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

suspend fun extractTextFromBitmap(bitmap: Bitmap?): String =
    suspendCancellableCoroutine { continuation ->
        if (bitmap == null) {
            continuation.resume("Error: Bitmap is null")
            return@suspendCancellableCoroutine
        }

        val recognizer: TextRecognizer = getClient(TextRecognizerOptions.Builder().build())
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(inputImage)
            .addOnSuccessListener { result ->
                continuation.resume(result.text)
            }
            .addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
    }

fun getBitmapFromUri(uri: Uri, contentResolver: ContentResolver): Bitmap? {
    return try {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


private fun generateBarCode(text: String?): Bitmap {
    val width = 500
    val height = 150
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val codeWriter = MultiFormatWriter()
    try {
        val bitMatrix = codeWriter.encode(
            text,
            BarcodeFormat.CODE_128,
            width,
            height
        )
        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                bitmap.setPixel(x, y, color)
            }
        }
    } catch (e: WriterException) {
        Log.d("TAG", "generateBarCode: ${e.message}")
    }
    return bitmap
}

private fun createSimpleBitmap(): Bitmap {

    val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint()
    paint.color = Color.BLACK
    paint.style = Paint.Style.FILL

    val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 40f
        textAlign = Paint.Align.CENTER
    }

    canvas.drawCircle(100f, 100f, 50f, paint)
    canvas.drawText("Error", 100f, 115f, textPaint)

    return bitmap
}
