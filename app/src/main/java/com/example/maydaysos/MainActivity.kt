package com.example.maydaysos

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.google.android.gms.location.*
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MayDaySOSTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
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
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            )
                        }
                    ) { padding ->
                        SOSContent(Modifier.padding(padding))
                    }
                }
            }
        }
    }
}

@Composable
fun SOSContent(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val fusedClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val phoneNumbers = remember { mutableStateListOf("9876543210") }
    val baseMessage = remember { mutableStateOf("🆘 EMERGENCY! I need immediate help.") }

    val smsPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    fun hasSmsPermission() =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED

    fun hasLocationPermission() =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    /** ✅ CONTACT VALIDATION */
    fun areContactsValid(): Boolean {
        if (phoneNumbers.isEmpty()) return false
        return phoneNumbers.none { it.trim().isEmpty() }
    }

    fun sendSmsWithLocation(lat: Double, lng: Double) {
        val finalMessage = """
            ${baseMessage.value}

            📍 Location:
            Latitude: $lat
            Longitude: $lng

            📌 Google Maps:
            https://maps.google.com/?q=$lat,$lng
        """.trimIndent()

        sendSmsDirectly(context, phoneNumbers, finalMessage)
    }

    fun sendSmsWithoutLocation() {
        sendSmsDirectly(
            context,
            phoneNumbers,
            "${baseMessage.value}\n\n📍 Location unavailable"
        )
    }

    fun fetchLocationAndSendSOS() {

        // 🔴 NEW VALIDATION CHECK
        if (!areContactsValid()) {
            Toast.makeText(
                context,
                "Please fill all contact numbers",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (!hasSmsPermission()) {
            smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
            Toast.makeText(context, "Allow SMS permission", Toast.LENGTH_SHORT).show()
            return
        }

        if (!hasLocationPermission()) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            Toast.makeText(context, "Allow Location permission", Toast.LENGTH_SHORT).show()
            return
        }

        val sent = AtomicBoolean(false)
        val handler = Handler(Looper.getMainLooper())

        // ⏱️ Hard timeout → guarantees SMS delivery
        handler.postDelayed({
            if (sent.compareAndSet(false, true)) {
                sendSmsWithoutLocation()
            }
        }, 5000)

        fusedClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->

            if (sent.get()) return@addOnSuccessListener

            if (location != null) {
                sent.set(true)
                handler.removeCallbacksAndMessages(null)
                sendSmsWithLocation(location.latitude, location.longitude)
            } else {
                fusedClient.lastLocation.addOnSuccessListener { lastLocation ->
                    if (sent.compareAndSet(false, true)) {
                        handler.removeCallbacksAndMessages(null)
                        if (lastLocation != null) {
                            sendSmsWithLocation(
                                lastLocation.latitude,
                                lastLocation.longitude
                            )
                        } else {
                            sendSmsWithoutLocation()
                        }
                    }
                }
            }
        }.addOnFailureListener {
            if (sent.compareAndSet(false, true)) {
                handler.removeCallbacksAndMessages(null)
                sendSmsWithoutLocation()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "🚨 EMERGENCY SOS 🚨",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD32F2F)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tap SOS to send message with location")
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text("Emergency Contacts", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(12.dp))

                phoneNumbers.forEachIndexed { index, number ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = number,
                            onValueChange = { phoneNumbers[index] = it },
                            label = { Text("Contact ${index + 1}") },
                            modifier = Modifier.weight(1f)
                        )

                        if (phoneNumbers.size > 1) {
                            IconButton(onClick = { phoneNumbers.removeAt(index) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = { phoneNumbers.add("") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("➕ Add Contact")
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = baseMessage.value,
                    onValueChange = { baseMessage.value = it },
                    label = { Text("Emergency Message") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { fetchLocationAndSendSOS() },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD32F2F),
                contentColor = Color.White
            ),
            modifier = Modifier.size(180.dp)
        ) {
            Text("SOS", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${phoneNumbers.first()}")
                }
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
        ) {
            Text("📞 Call Emergency", color = Color.White)
        }
    }
}

/**
 * ✅ Multipart-safe SMS sending
 */
fun sendSmsDirectly(
    context: Context,
    phoneNumbers: List<String>,
    message: String
) {
    try {
        val smsManager =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }

        phoneNumbers.forEach { number ->
            if (number.isNotBlank()) {
                val parts = smsManager.divideMessage(message)
                smsManager.sendMultipartTextMessage(
                    number,
                    null,
                    parts,
                    null,
                    null
                )
            }
        }

        Toast.makeText(context, "SOS message sent successfully", Toast.LENGTH_LONG).show()

    } catch (e: Exception) {
        Toast.makeText(context, "SMS failed: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

@Preview(showBackground = true)
@Composable
fun SOSPreview() {
    MayDaySOSTheme {
        SOSContent()
    }
}
