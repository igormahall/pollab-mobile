package com.example.enqueteapp.presentation.poll_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.enqueteapp.data.Enquete
import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollListScreen(
    navController: NavController,
    viewModel: PollListViewModel = viewModel()
) {
    // Este LaunchedEffect será executado sempre que a tela for exibida
    LaunchedEffect(key1 = Unit) {
        viewModel.fetchEnquetes()
    }

    val uiState by viewModel.uiState.collectAsState()

    // O Scaffold agora está AQUI, dentro da tela, e não na MainActivity.
    // Isso resolve o problema de sobreposição da barra de status.
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Enquetes Ativas") },
                actions = {
                    // Ícone para adicionar nova enquete
                    IconButton(onClick = { navController.navigate("enquetes/nova") }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Criar Nova Enquete")
                    }
                }
            )
        }
    ) { paddingValues ->
        // O conteúdo da tela é renderizado dentro deste Box, respeitando o padding da TopAppBar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is PollListUiState.Loading -> CircularProgressIndicator()
                is PollListUiState.Success -> {
                    if (state.enquetes.isEmpty()) {
                        Text("Nenhuma enquete disponível no momento.")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.enquetes) { enquete ->
                                PollListItem(
                                    enquete = enquete,
                                    onClick = {
                                        navController.navigate("poll_detail/${enquete.id}")
                                    }
                                )
                            }
                        }
                    }
                }
                is PollListUiState.Error -> Text(
                    text = "Falha ao carregar enquetes: ${state.message}",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun PollListItem(
    enquete: Enquete,
    onClick: () -> Unit
) {
    // Card com elevação para um look mais limpo
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = enquete.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${enquete.opcoes.size} opções • ${enquete.opcoes.sumOf { it.votos }} votos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}