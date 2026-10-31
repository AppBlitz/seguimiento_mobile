package co.edu.uniquindio.games.ui.pokemon_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import co.edu.uniquindio.games.core.di.AppModule
import co.edu.uniquindio.games.core.util.NetworkMonitor
import co.edu.uniquindio.games.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PokemonListViewModel(
    private val repository: PokemonRepository,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(PokemonListUiState())
    val uiState: StateFlow<PokemonListUiState> = _uiState.asStateFlow()

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    private var currentOffset = 0
    private val pageSize = 20

    init {
        loadInitialPage()
    }

    fun loadInitialPage() {
        currentOffset = 0
        _uiState.update {
            it.copy(
                isLoadingInitial = true,
                errorMessage = null,
                endReached = false
            )
        }
        viewModelScope.launch {
            repository.getPokemonPage(limit = pageSize, offset = 0)
                .onSuccess { items ->
                    currentOffset = pageSize
                    _uiState.update {
                        it.copy(
                            pokemonList = items,
                            isLoadingInitial = false,
                            endReached = items.isEmpty()
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingInitial = false,
                            errorMessage = "Error al cargar Pokémon: ${error.localizedMessage}"
                        )
                    }
                }
        }
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.isLoadingInitial || state.isLoadingMore || state.endReached || state.isFilterActive) {
            return
        }

        _uiState.update { it.copy(isLoadingMore = true, errorMessage = null) }
        viewModelScope.launch {
            repository.getPokemonPage(limit = pageSize, offset = currentOffset)
                .onSuccess { newItems ->
                    currentOffset += pageSize
                    _uiState.update { current ->
                        current.copy(
                            pokemonList = (current.pokemonList + newItems).distinctBy { it.id },
                            isLoadingMore = false,
                            endReached = newItems.isEmpty()
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingMore = false,
                            errorMessage = "No se pudieron cargar más elementos: ${error.localizedMessage}"
                        )
                    }
                }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
    }

    fun onTypeSelected(type: String?) {
        _uiState.update { it.copy(selectedType = type) }
        applyFilters()
    }

    fun onHabitatSelected(habitat: String?) {
        _uiState.update { it.copy(selectedHabitat = habitat) }
        applyFilters()
    }

    fun applyFilters() {
        val state = _uiState.value
        if (!state.isFilterActive) {
            loadInitialPage()
            return
        }

        _uiState.update { it.copy(isLoadingInitial = true, errorMessage = null) }
        viewModelScope.launch {
            repository.filterPokemon(
                query = state.searchQuery,
                type = state.selectedType,
                habitat = state.selectedHabitat
            ).onSuccess { filtered ->
                _uiState.update {
                    it.copy(
                        pokemonList = filtered,
                        isLoadingInitial = false
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoadingInitial = false,
                        errorMessage = "Error al filtrar: ${error.localizedMessage}"
                    )
                }
            }
        }
    }

    fun clearFilters() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedType = null,
                selectedHabitat = null
            )
        }
        loadInitialPage()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PokemonListViewModel(
                    repository = AppModule.pokemonRepository,
                    networkMonitor = AppModule.networkMonitor
                ) as T
            }
        }
    }
}