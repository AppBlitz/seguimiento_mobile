package co.edu.uniquindio.games.data.remote

import co.edu.uniquindio.games.data.remote.dto.PokemonDetailDto
import co.edu.uniquindio.games.data.remote.dto.PokemonHabitatResponseDto
import co.edu.uniquindio.games.data.remote.dto.PokemonListResponseDto
import co.edu.uniquindio.games.data.remote.dto.PokemonSpeciesDto
import co.edu.uniquindio.games.data.remote.dto.PokemonTypeResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {

    // ENDPOINT 1: Lista paginada de Pokémon (para scroll infinito)
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonListResponseDto

    // ENDPOINT 2: Filtrado por Campo 1 -> Tipo de Pokémon (fire, water, grass, etc.)
    @GET("type/{typeName}")
    suspend fun getPokemonByType(
        @Path("typeName") typeName: String
    ): PokemonTypeResponseDto

    // ENDPOINT 3: Filtrado por Campo 2 -> Hábitat del Pokémon (cave, forest, sea, etc.)
    @GET("pokemon-habitat/{habitatName}")
    suspend fun getPokemonByHabitat(
        @Path("habitatName") habitatName: String
    ): PokemonHabitatResponseDto

    // ENDPOINT 4: Detalle técnico del Pokémon o búsqueda directa por nombre/ID
    @GET("pokemon/{idOrName}")
    suspend fun getPokemonDetail(
        @Path("idOrName") idOrName: String
    ): PokemonDetailDto

    // ENDPOINT 5: Detalle de especie (descripción oficial en español, generación, legendario)
    @GET("pokemon-species/{id}")
    suspend fun getPokemonSpecies(
        @Path("id") id: Int
    ): PokemonSpeciesDto
}
