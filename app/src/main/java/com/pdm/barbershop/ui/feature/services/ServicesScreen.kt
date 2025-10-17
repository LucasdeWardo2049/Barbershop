package com.pdm.barbershop.ui.feature.services

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pdm.barbershop.domain.model.CatalogItem
import com.pdm.barbershop.ui.feature.services.components.ServiceCard
import com.pdm.barbershop.ui.feature.services.components.ServicesSegmentedControl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    viewModel: ServicesViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Serviços") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            when (val s = state) {
                is ServicesUiState.Loading -> {
                    // tela em branco durante loading
                }
                is ServicesUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = s.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is ServicesUiState.Content -> {
                    // Segmented buttons no topo
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        ServicesSegmentedControl(
                            selectedTab = s.selectedTab,
                            onSelect = viewModel::onTabSelected
                        )
                    }

                    val list: List<CatalogItem> = when (s.selectedTab) {
                        ServicesTab.Services -> s.services
                        ServicesTab.Products -> s.products
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 64.dp), // espaço abaixo do segmented
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(items = list, key = { it.id }) { item ->
                            ServiceCard(
                                item = item,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}