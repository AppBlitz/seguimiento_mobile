package co.edu.uniquindio.games.ui.pokemon_list

import co.edu.uniquindio.games.domain.model.PokemonItem

data class PokemonListUiState(
    val pokemonList: List<PokemonItem> = emptyList(),
    val isLoadingInitial: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedType: String? = null,
    val selectedHabitat: String? = null,
    val endReached: Boolean = false
) {
    val isFilterActive: Boolean
        get() = searchQuery.isNotBlank() || !selectedType.isNullOrBlank() || !selectedHabitat.isNullOrBlank()
}