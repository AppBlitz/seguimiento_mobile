package co.edu.uniquindio.games.ui.pokemon_detail

import co.edu.uniquindio.games.domain.model.PokemonDetail

data class PokemonDetailUiState(
    val isLoading: Boolean = false,
    val pokemon: PokemonDetail? = null,
    val errorMessage: String? = null
)