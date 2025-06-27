package com.example.app.presentation.poll_detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.data.Enquete
import com.example.app.data.Opcao
import com.example.app.ui.theme.extendedColors
import kotlinx.coroutines.flow.collectLatest
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun calcularTempoRestante(expiresAt: String?): String? {
    return try {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val expiration = OffsetDateTime.parse(expiresAt ?: return null, formatter)
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val duration = java.time.Duration.between(now, expiration)
        if (duration.isNegative) return null
        val horas = duration.toHours()
        val minutos = duration.toMinutes() % 60
        "%02dh %02dm".format(horas, minutos)
    } catch (e: Exception) {
        null
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollDetailScreen(
    pollId: String?,
    navController: NavController,
    viewModel: PollDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.voteEvent.collectLatest { event ->
            if (event is VoteEvent.ShowToast) {
                snackbarHostState.showSnackbar(event.message, duration = SnackbarDuration.Short)
            }
        }
    }

    LaunchedEffect(pollId) {
        pollId?.let { viewModel.fetchEnquete(it) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Lista de enquetes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is PollDetailUiState.Loading -> CircularProgressIndicator()
                is PollDetailUiState.Success -> {
                    val isExpired = viewModel.isEnqueteExpirada(state.enquete.expires_at)
                    PollDetailContent(
                        enquete = state.enquete,
                        isExpired = isExpired,
                        onVoteClick = { optionId, participantId -> viewModel.vote(optionId, participantId) }
                    )
                }
                is PollDetailUiState.Error -> Text("Falha ao carregar: ${state.message}")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollDetailContent(
    enquete: Enquete,
    isExpired: Boolean,
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

        val tempoRestante = calcularTempoRestante(enquete.expires_at)
        tempoRestante?.let {
            Text(
                text = "Expira em: $it",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp)
            )
        }

        if (!isExpired) {
            OutlinedTextField(
                value = participantName,
                onValueChange = { participantName = it },
                label = { Text("Seu Nome (para votar)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(autoCorrect = false),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )
        } else {
            AssistChip(
                onClick = {},
                label = {
                    Text(
                        "Enquete encerrada",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    leadingIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(enquete.opcoes) { opcao ->
                val isWinner = maxVotes > 0 && opcao.votos == maxVotes
                PollOptionItem(
                    opcao = opcao,
                    isWinner = isWinner,
                    isVotingEnabled = !isExpired && participantName.isNotBlank(),
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
    val backgroundColor = if (isWinner) {
        MaterialTheme.extendedColors.cardAberta.copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.surface
    }
    val borderColor = if (isWinner) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else Color.Transparent

    Card(
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(14.dp))
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = opcao.texto_opcao,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isWinner) FontWeight.SemiBold else FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Votos: ${opcao.votos}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isVotingEnabled) {
                Button(
                    onClick = onVoteClick,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.defaultMinSize(minWidth = 90.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Votar")
                }
            }
        }
    }
}