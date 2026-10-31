package co.edu.uniquindio.games.data.repository

import co.edu.uniquindio.games.data.remote.PokeApiService
import co.edu.uniquindio.games.data.remote.dto.mapToPokemonDetail
import co.edu.uniquindio.games.domain.model.PokemonDetail
import co.edu.uniquindio.games.domain.model.PokemonItem
import co.edu.uniquindio.games.domain.repository.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class PokemonRepositoryImpl(
    private val apiService: PokeApiService
) : PokemonRepository {

    override suspend fun getPokemonPage(limit: Int, offset: Int): Result<List<PokemonItem>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = apiService.getPokemonList(limit = limit, offset = offset)
                response.results
                    .map { it.toDomainItem() }
                    .filter { it.id in 1..1025 }
            }
        }

    override suspend fun filterPokemon(
        query: String,
        type: String?,
        habitat: String?
    ): Result<List<PokemonItem>> = withContext(Dispatchers.IO) {
        runCatching {
            val cleanQuery = query.trim().lowercase()

            if (type.isNullOrBlank() && habitat.isNullOrBlank() && cleanQuery.isNotEmpty()) {
                return@runCatching try {
                    val exactPokemon = apiService.getPokemonDetail(cleanQuery)
                    listOf(exactPokemon.toDomainItem())
                } catch (e: Exception) {
                    val catalog = apiService.getPokemonList(limit = 1025, offset = 0)
                    catalog.results
                        .map { it.toDomainItem() }
                        .filter { it.name.lowercase().contains(cleanQuery) }
                }
            }

            val byTypeDeferred = if (!type.isNullOrBlank()) {
                async {
                    apiService.getPokemonByType(type).pokemon
                        .map { it.pokemon.toDomainItem() }
                        .filter { it.id in 1..1025 }
                }
            } else null

            val byHabitatDeferred = if (!habitat.isNullOrBlank()) {
                async {
                    apiService.getPokemonByHabitat(habitat).pokemonSpecies
                        .map { it.toDomainItem() }
                        .filter { it.id in 1..1025 }
                }
            } else null

            val typeList = byTypeDeferred?.await()
            val habitatList = byHabitatDeferred?.await()

            var combinedList: List<PokemonItem> = when {
                typeList != null && habitatList != null -> {
                    val habitatIds = habitatList.map { it.id }.toSet()
                    typeList.filter { it.id in habitatIds }
                }
                typeList != null -> typeList
                habitatList != null -> habitatList
                else -> emptyList()
            }

            if (cleanQuery.isNotEmpty()) {
                combinedList = combinedList.filter {
                    it.name.lowercase().contains(cleanQuery) || it.id.toString() == cleanQuery
                }
            }

            combinedList.sortedBy { it.id }
        }
    }

    override suspend fun getPokemonDetail(id: Int): Result<PokemonDetail> =
        withContext(Dispatchers.IO) {
            runCatching {
                val detailDeferred = async { apiService.getPokemonDetail(id.toString()) }
                val speciesDeferred = async {
                    runCatching { apiService.getPokemonSpecies(id) }.getOrNull()
                }
                mapToPokemonDetail(detailDeferred.await(), speciesDeferred.await())
            }
        }
}