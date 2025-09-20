package com.pdm.barbershop.ui.feature.help

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajuda e Suporte") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                Text("Perguntas Frequentes", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                HelpItem("Como faço para agendar um horário?", "Vá para a tela 'Agenda', selecione um barbeiro, um serviço e um horário disponível. Depois, confirme o agendamento.")
            }
            item {
                HelpItem("Como posso cancelar um agendamento?", "Na tela 'Meus Agendamentos', encontre o agendamento desejado e clique na opção para cancelar.")
            }
            item {
                Text("Contato", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 24.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Se precisar de mais ajuda, entre em contato:\nEmail: suporte@barbershoppro.com\nTelefone: (92) 99999-8888", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun HelpItem(question: String, answer: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(question, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(answer, style = MaterialTheme.typography.bodyLarge)
    }
}
