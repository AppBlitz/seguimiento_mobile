package co.edu.uniquindio.games.ui.pokemon_detail

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

class PokemonDetailViewModel(
    private val pokemonId: Int,
    private val repository: PokemonRepository,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(PokemonDetailUiState())
    val uiState: StateFlow<PokemonDetailUiState> = _uiState.asStateFlow()

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    init {
        loadPokemonDetail()
    }

    fun loadPokemonDetail() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            repository.getPokemonDetail(pokemonId)
                .onSuccess { detail ->
                    _uiState.update {
                        it.copy(isLoading = false, pokemon = detail)
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "No se pudo cargar el detalle: ${error.localizedMessage}"
                        )
                    }
                }
        }
    }

    companion object {
        fun provideFactory(pokemonId: Int): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PokemonDetailViewModel(
                        pokemonId = pokemonId,
                        repository = AppModule.pokemonRepository,
                        networkMonitor = AppModule.networkMonitor
                    ) as T
                }
            }
    }
}