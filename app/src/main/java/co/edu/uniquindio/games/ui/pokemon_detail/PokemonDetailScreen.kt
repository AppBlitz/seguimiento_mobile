package co.edu.uniquindio.games.ui.pokemon_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.games.ui.components.NetworkStatusBanner
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    viewModel: PokemonDetailViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(uiState.pokemon?.name ?: "Detalle del Pokémon") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                )
                NetworkStatusBanner(isOnline = isOnline)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(uiState.errorMessage ?: "Error")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = viewModel::loadPokemonDetail) {
                            Text("Reintentar")
                        }
                    }
                }
                uiState.pokemon != null -> {
                    val pokemon = uiState.pokemon!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = pokemon.imageUrl,
                            contentDescription = pokemon.name,
                            modifier = Modifier.size(180.dp)
                        )
                        Text(
                            text = "#${pokemon.id} ${pokemon.name}",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            pokemon.types.forEach { type ->
                                AssistChip(onClick = {}, label = { Text(type) })
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Descripción", style = MaterialTheme.typography.titleMedium)
                                Text(pokemon.description, style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Altura: ${pokemon.heightInMeters} m | Peso: ${pokemon.weightInKg} kg")
                                Text("Hábitat: ${pokemon.habitat} | Generación: ${pokemon.generation}")
                                Text("Habilidades: ${pokemon.abilities.joinToString(", ")}")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Estadísticas Base",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        pokemon.stats.forEach { stat ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(stat.name, style = MaterialTheme.typography.bodySmall)
                                    Text("${stat.value} / 255", style = MaterialTheme.typography.bodySmall)
                                }
                                LinearProgressIndicator(
                                    progress = { (stat.value / 255f).coerceIn(0f, 1f) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}