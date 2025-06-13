package com.example.enqueteapp.presentation.poll_form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.material3.TextFieldDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnqueteFormComponent(
    navController: NavController,
    viewModel: PollFormViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    val options = remember { mutableStateListOf("", "") } // Começa com 2 opções
    val snackbarHostState = remember { SnackbarHostState() }
    var isSubmitting by remember { mutableStateOf(false) }

    // Ouve eventos do ViewModel para navegação ou erros
    LaunchedEffect(key1 = Unit) {
        viewModel.formEvent.collectLatest { event ->
            isSubmitting = false
            when (event) {
                is FormEvent.Success -> {
                    navController.navigate("poll_detail/${event.newPollId}") {
                        // Limpa a pilha de navegação até a tela de lista
                        popUpTo("poll_list")
                    }
                }
                is FormEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Criar Nova Enquete") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            // Botão flutuante para salvar
            FloatingActionButton(onClick = {
                isSubmitting = true
                viewModel.createEnquete(title, options)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Salvar Enquete")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título da enquete") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }

            itemsIndexed(options) { index, option ->
                OutlinedTextField(
                    value = option,
                    onValueChange = { options[index] = it },
                    label = { Text("Opção ${index + 1}") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (options.size > 2) {
                            IconButton(onClick = { options.removeAt(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remover Opção")
                            }
                        }
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }

            item {
                Button(
                    onClick = { options.add("") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Adicionar mais opções")
                }
            }
        }
    }
}