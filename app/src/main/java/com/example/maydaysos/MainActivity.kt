package com.example.maydaysos

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.maydaysos.ui.theme.MayDaySOSTheme

/*
 * ================================= IMPORTANT =================================
 * You MUST add the following line to your AndroidManifest.xml file
 * for the SMS functionality to work. Add it just before the <application> tag.
 *
 * <uses-permission android:name="android.permission.SEND_SMS" />
 *
 * ===========================================================================
 */

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MayDaySOSTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color(0xFF4CAF50),
                                    titleContentColor = Color.White
                                ),
                                title = {
                                    Text(
                                        text = "MayDay SOS",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            )
                        }
                    ) { paddingValues ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            // We pass the context from the activity
                            SOSContent()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SOSContent() {
    // Current context is needed for permissions and sending SMS
    val context = LocalContext.current

    // State for emergency contacts and message
    val phoneNumber1 = remember { mutableStateOf("9777548904") }
    val phoneNumber2 = remember { mutableStateOf("8594937782") }
    val message = remember { mutableStateOf("🆘 EMERGENCY! I need immediate help. This is an automated SOS message. Please contact me or call emergency services.") }

    // --- New Permission Handling Logic ---
    // This launcher will request the SEND_SMS permission.
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // If permission is granted, inform the user.
            Toast.makeText(context, "Permission granted. You can now send SOS messages.", Toast.LENGTH_SHORT).show()
        } else {
            // If permission is denied, inform the user.
            Toast.makeText(context, "Permission denied. Cannot send SOS messages.", Toast.LENGTH_SHORT).show()
        }
    }

    // This function checks for permission and then sends the SMS.
    fun checkAndSendSms(phoneNumbers: List<String>) {
        when {
            // Check if the permission is already granted
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // If granted, send the SMS
                sendSmsDirectly(context, phoneNumbers, message.value)
            }
            // If permission is not granted, request it.
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.SEND_SMS)
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Emergency Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🚨 EMERGENCY SOS 🚨",
                    color = Color(0xFFD32F2F),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap the SOS button to send emergency messages",
                    color = Color(0xFF666666),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Contact Information Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Emergency Contacts",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = phoneNumber1.value,
                    onValueChange = { phoneNumber1.value = it },
                    label = { Text("Contact 1") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phoneNumber2.value,
                    onValueChange = { phoneNumber2.value = it },
                    label = { Text("Contact 2") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = message.value,
                    onValueChange = { message.value = it },
                    label = { Text("Emergency Message") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 4
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // SOS Button (Large and Prominent)
        Button(
            onClick = {
                // Now calls the function that checks permissions first
                checkAndSendSms(listOf(phoneNumber1.value))
            },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD32F2F),
                contentColor = Color.White
            ),
            modifier = Modifier.size(180.dp)
        ) {
            Text(
                "SOS",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Call Emergency Button (This logic remains the same)
            Button(
                onClick = {
                    callEmergency(context, phoneNumber1.value)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("📞 Call", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Send to Both Contacts Button
            Button(
                onClick = {
                    // Now calls the function that checks permissions first for both numbers
                    checkAndSendSms(listOf(phoneNumber1.value, phoneNumber2.value))
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("📨 Send All", fontSize = 16.sp)
            }
        }
    }
}

/**
 * Sends an SMS message directly using SmsManager.
 * @param context The application context.
 * @param phoneNumbers A list of phone numbers to send the message to.
 * @param message The message content.
 */
fun sendSmsDirectly(context: Context, phoneNumbers: List<String>, message: String) {
    if (message.isBlank()) {
        Toast.makeText(context, "Emergency message cannot be empty.", Toast.LENGTH_SHORT).show()
        return
    }

    try {
        // Get the default SmsManager instance
        val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(SmsManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }

        var numbersSent = 0
        // Loop through each phone number and send the message
        phoneNumbers.forEach { phoneNumber ->
            if (phoneNumber.isNotBlank()) {
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                numbersSent++
            }
        }

        if (numbersSent > 0) {
            Toast.makeText(context, "SOS message sent to $numbersSent contact(s)!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "No valid phone numbers provided.", Toast.LENGTH_SHORT).show()
        }

    } catch (e: Exception) {
        Toast.makeText(context, "Failed to send SMS. Error: ${e.message}", Toast.LENGTH_LONG).show()
        e.printStackTrace()
    }
}

/**
 * Opens the dialer app with the specified phone number.
 * This is kept as is, as it's good practice to let the user confirm the call.
 * @param context The application context.
 * @param phoneNumber The number to call.
 */
fun callEmergency(context: Context, phoneNumber: String) {
    if (phoneNumber.isBlank()) {
        Toast.makeText(context, "Phone number is empty.", Toast.LENGTH_SHORT).show()
        return
    }
    try {
        val callIntent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        context.startActivity(callIntent)
    } catch (e: Exception) {
        Toast.makeText(context, "Error making call: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}


@Preview(showBackground = true)
@Composable
fun SOSContentPreview() {
    MayDaySOSTheme {
        SOSContent()
    }
}
