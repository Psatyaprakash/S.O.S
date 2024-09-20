package com.example.maydaysos

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maydaysos.ui.theme.MayDaySOSTheme
import com.example.maydaysos.ui.theme.greenColor

//import com.github.skydoves.colorpicker.compose.*

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MayDaySOSTheme {
                // on below line we are specifying background color for our application
                Surface(
                    // on below line we are specifying modifier and color for our app
                    modifier = Modifier.fillMaxSize(),
                ) {

                    // on the below line we are specifying
                    // the theme as the scaffold.
                    Scaffold(

                        // in scaffold we are specifying the top bar.
                        topBar = {

                            // inside top bar we are specifying
                            // background color.
                            TopAppBar(colors = TopAppBarColors(containerColor = greenColor, scrolledContainerColor = Color.Black, titleContentColor = Color.Cyan, actionIconContentColor = Color.White, navigationIconContentColor = Color.Transparent),

                                // along with that we are specifying
                                // title for our top bar.
                                title = {

                                    // in the top bar we are specifying
                                    // tile as a text
                                    Text(
                                        // on below line we are specifying
                                        // text to display in top app bar.
                                        text = "MayDay SOS",

                                        // on below line we are specifying
                                        // modifier to fill max width.
                                        modifier = Modifier.fillMaxWidth(),

                                        // on below line we are specifying
                                        // text alignment.
                                        textAlign = TextAlign.Center,

                                        // on below line we are specifying
                                        // color for our text.
                                        color = Color.White
                                    )
                                })
                        }) {
                        // on below line we are calling connection
                        // information method to display UI
                        smsUI(context = LocalContext.current)
                    }
                }
            }
        }
    }
}

@Composable
fun smsUI(context: Context) {
    // on below line creating variable for
    // service status and button value.
    val phoneNumber = remember {
        mutableStateOf("")
    }
    val message = remember {
        mutableStateOf("")
    }

    // on below line we are creating a column,
    Column(
        // on below line we are adding a modifier to it,
        modifier = Modifier
            .fillMaxSize()
            // on below line we are adding a padding.
            .padding(all = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // on below line we are adding a text for heading.
        Text(
            // on below line we are specifying text
            text = "SMS Manager in Android",
            // on below line we are specifying text color,
            // font size and font weight
            color = greenColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        // on below line we are creating a text field for our phone number.
        TextField(
            // on below line we are specifying value for our email text field.
            value = phoneNumber.value,
            // on below line we are adding on value change for text field.
            onValueChange = { phoneNumber.value = it },
            // on below line we are adding place holder as text
            // as "Enter your email"
            placeholder = { Text(text = "Enter your phone number") },
            // on below line we are adding modifier to it
            // and adding padding to it and filling max width
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            // on below line we are adding text style
            // specifying color and font size to it.
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            // on below line we ar adding single line to it.
            singleLine = true,
        )
        // on below line we are adding a spacer.
        Spacer(modifier = Modifier.height(20.dp))

        // on below line we are creating a text field for our message number.
        TextField(
            // on below line we are specifying value for our message text field.
            value = message.value,
            // on below line we are adding on value change for text field.
            onValueChange = { message.value = it },
            // on below line we are adding place holder as text as "Enter your email"
            placeholder = { Text(text = "Enter your message") },
            // on below line we are adding modifier to it
            // and adding padding to it and filling max width
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            // on below line we are adding text style
            // specifying color and font size to it.
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            // on below line we are adding single line to it.
            singleLine = true,
        )
        // on below line adding a spacer.
        Spacer(modifier = Modifier.height(20.dp))
        // on below line adding a button to send SMS
        Button(onClick = {
            // on below line running a try catch block for sending sms.
            try {
                // on below line initializing sms manager.
                val smsManager: SmsManager = SmsManager.getDefault()
                // on below line sending sms
                smsManager.sendTextMessage(phoneNumber.value, null, message.value, null, null)
                // on below line displaying
                // toast message as sms send.
                Toast.makeText(
                    context,
                    "Message Sent",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                // on below line handling error and
                // displaying toast message.
                Toast.makeText(
                    context,
                    "Error : " + e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }) {
            // on below line creating a text for our button.
            Text(
                // on below line adding a text ,
                // padding, color and font size.
                text = "Send SOS",
                modifier = Modifier.padding(10.dp),
                color = Color.White,
                fontSize = 15.sp
            )
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
    MayDaySOSTheme {
        Greeting("Android")
    }
}