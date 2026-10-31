package co.edu.uniquindio.games.domain.repository

import co.edu.uniquindio.games.domain.model.PokemonDetail
import co.edu.uniquindio.games.domain.model.PokemonItem

interface PokemonRepository {
    suspend fun getPokemonPage(limit: Int, offset: Int): Result<List<PokemonItem>>
    suspend fun filterPokemon(query: String, type: String?, habitat: String?): Result<List<PokemonItem>>
    suspend fun getPokemonDetail(id: Int): Result<PokemonDetail>
}