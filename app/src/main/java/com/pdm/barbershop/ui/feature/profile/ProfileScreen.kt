package com.pdm.barbershop.ui.feature.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.pdm.barbershop.ui.theme.BarbershopTheme

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onEditProfileClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onComandasClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Launcher para buscar uma imagem da galeria
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onProfileImageChanged(uri)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Seção de Informações do Perfil
        ProfileHeader(
            userName = uiState.userName,
            userEmail = uiState.userEmail,
            profileImageUri = uiState.profileImageUri,
            onImageClick = {
                // Lança o seletor de imagens
                imagePickerLauncher.launch("image/*")
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Menu de Opções da Conta
        Text("Minha Conta", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column {
                ProfileMenuItem(icon = Icons.Default.Edit, text = "Alterar Dados Pessoais", onClick = onEditProfileClick)
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                ProfileMenuItem(icon = Icons.Default.DateRange, text = "Meus Agendamentos", onClick = onAppointmentsClick)
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                ProfileMenuItem(icon = Icons.Default.History, text = "Histórico de Comandas", onClick = onComandasClick)
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                ProfileMenuItem(icon = Icons.Default.Notifications, text = "Notificações", onClick = onNotificationsClick)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Menu de Ajuda e Informações
        Text("Ajuda & Informações", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column {
                ProfileMenuItem(icon = Icons.Default.HelpOutline, text = "Ajuda e Suporte", onClick = onHelpClick)
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                ProfileMenuItem(icon = Icons.Default.Info, text = "Sobre o App", onClick = onAboutClick)
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                ProfileMenuItem(icon = Icons.AutoMirrored.Filled.ExitToApp, text = "Sair", isLogout = true, onClick = { viewModel.logout() })
            }
        }
    }
}

@Composable
fun ProfileHeader(
    userName: String,
    userEmail: String,
    profileImageUri: Uri?, // Recebe a URI da imagem
    onImageClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            // Se a URI existir, mostra a imagem selecionada. Senão, mostra o ícone padrão.
            if (profileImageUri != null) {
                AsyncImage(
                    model = profileImageUri,
                    contentDescription = "Foto do Perfil",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable { onImageClick() },
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Foto do Perfil",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .clickable { onImageClick() },
                    contentScale = ContentScale.Crop
                )
            }

            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Alterar foto",
                tint = Color.White,
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(6.dp)
                    .clickable { onImageClick() }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = userName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = userEmail,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    text: String,
    isLogout: Boolean = false,
    onClick: () -> Unit
) {
    val textColor = if (isLogout) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = textColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        )
        if (!isLogout) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ir para $text",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    BarbershopTheme(useDarkTheme = true) {
        ProfileScreen(
            onEditProfileClick = {},
            onAppointmentsClick = {},
            onComandasClick = {},
            onNotificationsClick = {},
            onHelpClick = {},
            onAboutClick = {}
        )
    }
}