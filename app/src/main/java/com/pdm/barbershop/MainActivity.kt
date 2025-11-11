package com.pdm.barbershop

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pdm.barbershop.ui.AppScaffold
import com.pdm.barbershop.ui.theme.BarbershopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enableEdgeToEdge()
        }
        setContent {
            BarbershopTheme {
                AppScaffold()
            }
        }
    }
}
