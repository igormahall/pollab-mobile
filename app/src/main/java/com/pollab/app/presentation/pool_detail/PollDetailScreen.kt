package com.example.app.presentation.poll_detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.data.Enquete
import com.example.app.data.Opcao
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollDetailScreen(
    pollId: String?,
    navController: NavController,
    viewModel: PollDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Ouve os eventos do ViewModel para mostrar notificações de erro de voto
    LaunchedEffect(key1 = Unit) {
        viewModel.voteEvent.collectLatest { event ->
            when (event) {
                is VoteEvent.ShowToast -> {
                    snackbarHostState.showSnackbar(message = event.message, duration = SnackbarDuration.Short)
                }
            }
        }
    }

    LaunchedEffect(key1 = pollId) {
        if (pollId != null) {
            viewModel.fetchEnquete(pollId)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detalhes da Enquete") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is PollDetailUiState.Loading -> CircularProgressIndicator()
                is PollDetailUiState.Success -> PollDetailContent(
                    enquete = state.enquete,
                    onVoteClick = { optionId, participantId ->
                        viewModel.vote(optionId, participantId)
                    }
                )
                is PollDetailUiState.Error -> Text("Falha ao carregar: ${state.message}")
            }
        }
    }
}

@Composable
fun PollDetailContent(
    enquete: Enquete,
    onVoteClick: (optionId: Int, participantId: String) -> Unit
) {
    var participantName by remember { mutableStateOf("") }
    val maxVotes = enquete.opcoes.maxOfOrNull { it.votos } ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = enquete.titulo,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 16.dp)
        )

        OutlinedTextField(
            value = participantName,
            onValueChange = { participantName = it },
            label = { Text("Seu Nome (para votar)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            singleLine = true,
            enabled = enquete.status == "Aberta",
            keyboardOptions = KeyboardOptions(autoCorrect = false)
        )

        if (enquete.status == "Fechada") {
            Text(
                text = "Esta enquete está fechada para novos votos.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(enquete.opcoes) { opcao ->
                val isWinner = maxVotes > 0 && opcao.votos == maxVotes
                PollOptionItem(
                    opcao = opcao,
                    isWinner = isWinner,
                    isVotingEnabled = enquete.status == "Aberta" && participantName.isNotBlank(),
                    onVoteClick = { onVoteClick(opcao.id, participantName.trim()) }
                )
            }
        }
    }
}

@Composable
fun PollOptionItem(
    opcao: Opcao,
    isWinner: Boolean,
    isVotingEnabled: Boolean,
    onVoteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        border = if (isWinner) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = opcao.texto_opcao,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Votos: ${opcao.votos}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onVoteClick,
                enabled = isVotingEnabled,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.defaultMinSize(minWidth = 90.dp)
            ) {
                Text("Votar")
            }
        }
    }
}