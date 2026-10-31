package co.edu.uniquindio.games.data.remote

import co.edu.uniquindio.games.data.remote.dto.HabitatResponseDto
import co.edu.uniquindio.games.data.remote.dto.PokemonDetailDto
import co.edu.uniquindio.games.data.remote.dto.PokemonListResponseDto
import co.edu.uniquindio.games.data.remote.dto.PokemonSpeciesDto
import co.edu.uniquindio.games.data.remote.dto.TypeResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {

    // Endpoint 1: Lista paginada de Pokémon (para Scroll Infinito)
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonListResponseDto

    // Endpoint 2: Detalle o búsqueda exacta por nombre o ID
    @GET("pokemon/{nameOrId}")
    suspend fun getPokemonDetail(
        @Path("nameOrId") nameOrId: String
    ): PokemonDetailDto

    // Endpoint 3: Información de especie (descripción, hábitat y generación para el detalle)
    @GET("pokemon-species/{id}")
    suspend fun getPokemonSpecies(
        @Path("id") id: Int
    ): PokemonSpeciesDto

    // Endpoint 4: Filtrar Pokémon por Tipo (fire, water, grass, etc.)
    @GET("type/{type}")
    suspend fun getPokemonByType(
        @Path("type") type: String
    ): TypeResponseDto

    // Endpoint 5: Filtrar Pokémon por Hábitat (cave, forest, sea, etc.)
    @GET("pokemon-habitat/{habitat}")
    suspend fun getPokemonByHabitat(
        @Path("habitat") habitat: String
    ): HabitatResponseDto
}