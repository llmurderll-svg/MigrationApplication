package com.example.migrationapplication

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.migrationapplication.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->

            if (isGranted) {
                Toast.makeText(this, "Granted Location Permission", Toast.LENGTH_LONG).show()
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
                    goToSettings()
                }
                else{
                    Toast.makeText(this, "Permiso denegeado", Toast.LENGTH_LONG).show()
                }
            }
        }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        binding.btnPermissionsSolicitude.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Esta app se necesita")
                .setMessage("en ....")
                .setPositiveButton(
                    "OK"
                ) { _, _ ->
                    checkLocationPermission()
                }
                .create()
                .show()

        }
    }

    private fun checkLocationPermission(){
        val isFineLocationGranted = ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        val isCoarseLocationGranted = ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,ACCESS_FINE_LOCATION)
        if (isFineLocationGranted || isCoarseLocationGranted ){
            if(shouldShowRationale){
                AlertDialog.Builder(this)
                    .setTitle("Por favor")
                    .setMessage("ACEPTAR :C")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            }
            else{
                requestLocationPermission()
            }
        }
       else{
            Toast.makeText(this, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show()
       }
    }

    private fun requestLocationPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION,), MY_PERMISSIONS_REQUEST_LOCATION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Granted Location Permission", Toast.LENGTH_LONG).show()
                    }
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
                        AlertDialog.Builder(this)
                            .setTitle("Requiere ir a opciones")
                            .setMessage("Settings")
                            .setPositiveButton(
                                "OK"
                            ) { _, _ ->
                                goToSettings()
                            }
                            .create()
                            .show()

                    }
                }
                return
            }
        }
    }

    private fun goToSettings(){
        val uri = Uri.fromParts("package", this.packageName, null)
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri )
        startActivity(intent)
    }
}

fun <K, V> Map<out K, V>.getOrDefaultCompat(key: K, defaultValue: V): V {
    if (this.containsKey(key)) {
        return this[key] ?: defaultValue
    }
    return defaultValue
}