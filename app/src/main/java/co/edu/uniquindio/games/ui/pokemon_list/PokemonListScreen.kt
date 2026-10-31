package co.edu.uniquindio.games.ui.pokemon_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.edu.uniquindio.games.domain.model.PokemonItem
import co.edu.uniquindio.games.ui.components.NetworkStatusBanner
import coil.compose.AsyncImage

private val POKEMON_TYPES = listOf("fire", "water", "grass", "electric", "psychic", "dragon")
private val POKEMON_HABITATS = listOf("cave", "forest", "grassland", "mountain", "sea", "urban")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    viewModel: PokemonListViewModel,
    onPokemonClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisibleIndex >= totalItems - 4
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.loadNextPage()
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("PokéAPI Explorer") })
                NetworkStatusBanner(isOnline = isOnline)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Buscador por nombre o ID + botón de buscar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    modifier = Modifier.weight(1f),
                    label = { Text("Buscar por nombre o ID") },
                    singleLine = true,
                    trailingIcon = {
                        if (uiState.isFilterActive) {
                            IconButton(onClick = viewModel::clearFilters) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpiar filtros")
                            }
                        }
                    }
                )
                IconButton(onClick = viewModel::applyFilters) {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filtro Campo 1: Tipo (Endpoint /type/{type})
            Text("Filtrar por Tipo:", style = MaterialTheme.typography.labelLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(POKEMON_TYPES) { type ->
                    val selected = uiState.selectedType == type
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.onTypeSelected(if (selected) null else type) },
                        label = { Text(type.replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            // Filtro Campo 2: Hábitat (Endpoint /pokemon-habitat/{habitat})
            Text("Filtrar por Hábitat:", style = MaterialTheme.typography.labelLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(POKEMON_HABITATS) { habitat ->
                    val selected = uiState.selectedHabitat == habitat
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.onHabitatSelected(if (selected) null else habitat) },
                        label = { Text(habitat.replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when {
                uiState.isLoadingInitial -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.errorMessage != null && uiState.pokemonList.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = uiState.errorMessage ?: "Error desconocido")
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = viewModel::applyFilters) {
                            Text("Reintentar")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.pokemonList, key = { it.id }) { pokemon ->
                            PokemonRowCard(
                                pokemon = pokemon,
                                onClick = { onPokemonClick(pokemon.id) }
                            )
                        }

                        if (uiState.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PokemonRowCard(
    pokemon: PokemonItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier.size(72.dp)
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = "#${pokemon.id} - ${pokemon.name}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Toca para ver estadísticas, hábitat y descripción",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}