package com.my.ca

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.function.Consumer

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), 10)
    }



    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @SuppressLint("MissingPermission")
    fun checkCarrierRestrictionStatus() {
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        // Define an executor for background processing
        val executor: Executor = Executors.newSingleThreadExecutor()

        // Get the carrier restriction status
        telephonyManager.getCarrierRestrictionStatus(executor, Consumer { status ->
            val carrierStatusMessage = when (status) {
                TelephonyManager.CARRIER_RESTRICTION_STATUS_RESTRICTED_TO_CALLER -> "Restricted to Caller"
                TelephonyManager.CARRIER_RESTRICTION_STATUS_RESTRICTED -> "Restricted"
                TelephonyManager.CARRIER_RESTRICTION_STATUS_NOT_RESTRICTED -> "Not Restricted"
                TelephonyManager.CARRIER_RESTRICTION_STATUS_UNKNOWN -> "Unknown Restriction Status"
                else -> "Unknown Status Code"
            }

            // Log the carrier restriction status
            Log.d("CarrierStatusChecker", "Carrier Restriction Status: $carrierStatusMessage")

            // Show a toast message
            Toast.makeText(this, "Carrier Restriction Status: $carrierStatusMessage", Toast.LENGTH_LONG).show()
        })
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            10 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, now proceed with the carrier restriction status check

                    try {
                        checkCarrierRestrictionStatus()
                    } catch (e : Exception){
                        Toast.makeText(this,"Ex : " + e.message,Toast.LENGTH_LONG).show()
                    }

                } else {
                    // Permission denied
                    Toast.makeText(this, "Phone state permission is required to check carrier restriction status.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
