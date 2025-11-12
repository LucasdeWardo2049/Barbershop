package com.pdm.barbershop

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge // Importar esta linha
import androidx.annotation.RequiresApi
import com.pdm.barbershop.ui.AppScaffold
import com.pdm.barbershop.ui.theme.BarbershopTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Adicionar esta linha antes de setContent
        setContent {
            BarbershopTheme {
                AppScaffold()
            }
        }
    }
}