package com.example.app.presentation.poll_list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.data.Enquete
import com.example.app.ui.theme.extendedColors
import com.pollab.app.R

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.Duration
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollabTopBar() {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.pollab_home),
                    contentDescription = "Logo Pollab",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 12.dp)
                )
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Pollab",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Participe. Experimente. Transforme.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PollListScreen(
    navController: NavController,
    viewModel: PollListViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.fetchEnquetes()
    }

    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refresh() }
    )

    Scaffold(
        topBar = { PollabTopBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("enquetes/nova") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.padding(end = 16.dp, bottom = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Criar Nova Enquete")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
        ) {
            when (val state = uiState) {
                is PollListUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is PollListUiState.Success -> {
                    if (state.enquetes.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Nenhuma enquete disponível no momento.")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.enquetes) { enquete ->
                                PollListItem(
                                    enquete = enquete,
                                    viewModel = viewModel,
                                    onClick = {
                                        navController.navigate("poll_detail/${enquete.id}")
                                    }
                                )
                            }
                        }
                    }
                }

                is PollListUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Falha ao carregar enquetes: ${state.message}",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PollListItem(
    enquete: Enquete,
    onClick: () -> Unit,
    viewModel: PollListViewModel,
    modifier: Modifier = Modifier
) {
    val isExpired = viewModel.isEnqueteExpirada(enquete.expires_at)
    val isAberta = !isExpired

    val backgroundColor = if (isAberta) {
        MaterialTheme.extendedColors.cardAberta
    } else {
        MaterialTheme.extendedColors.cardFechada
    }

    val chipTextColor = if (isAberta) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    val chipBgColor = MaterialTheme.colorScheme.surface
    val iconTint = chipTextColor

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = enquete.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AssistChip(

                    onClick = {},
                    label = {
                        Text(
                            text = if (isAberta) "Aberta" else "Encerrada",
                            color = chipTextColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isAberta) Icons.Filled.PlayArrow else Icons.Filled.Lock,
                            contentDescription = null,
                            tint = iconTint
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = chipBgColor,
                        labelColor = chipTextColor,
                        leadingIconContentColor = iconTint
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "• ${enquete.opcoes.sumOf { it.votos }} votos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            val tempoRestante = calcularTempoRestante(enquete.expires_at)

            tempoRestante?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Expira em: $it",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun calcularTempoRestante(expiration: String?): String? {
    return try {
        if (expiration.isNullOrBlank()) return null
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val expiresAt = OffsetDateTime.parse(expiration, formatter)
        val now = OffsetDateTime.now(ZoneOffset.UTC)

        val duration = Duration.between(now, expiresAt)
        if (duration.isNegative) return null

        val horas = duration.toHours()
        val minutos = duration.toMinutes() % 60
        "%02dh %02dm".format(horas, minutos)
    } catch (e: Exception) {
        null
    }
}
