package co.edu.uniquindio.games.ui.pokemon_list

import co.edu.uniquindio.games.domain.model.FilterOption
import co.edu.uniquindio.games.domain.model.PokemonItem

data class PokemonListUiState(
    val pokemonList: List<PokemonItem> = emptyList(),
    val isInitialLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedType: String? = null,
    val selectedHabitat: String? = null,
    val endReached: Boolean = false
) {
    val isFilterActive: Boolean
        get() = searchQuery.isNotBlank() || selectedType != null || selectedHabitat != null
}

val POKEMON_TYPES = listOf(
    FilterOption("fire", "Fuego"),
    FilterOption("water", "Agua"),
    FilterOption("grass", "Planta"),
    FilterOption("electric", "Eléctrico"),
    FilterOption("psychic", "Psíquico"),
    FilterOption("dragon", "Dragón"),
    FilterOption("fighting", "Lucha"),
    FilterOption("ghost", "Fantasma"),
    FilterOption("fairy", "Hada")
)

val POKEMON_HABITATS = listOf(
    FilterOption("cave", "Cueva"),
    FilterOption("forest", "Bosque"),
    FilterOption("grassland", "Pradera"),
    FilterOption("mountain", "Montaña"),
    FilterOption("sea", "Mar"),
    FilterOption("urban", "Urbano"),
    FilterOption("waters-edge", "Orilla de agua")
)
