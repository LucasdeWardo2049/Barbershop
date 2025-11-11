package com.pdm.barbershop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge // Importar esta linha
import com.pdm.barbershop.ui.AppScaffold
import com.pdm.barbershop.ui.theme.BarbershopTheme

class MainActivity : ComponentActivity() {
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
