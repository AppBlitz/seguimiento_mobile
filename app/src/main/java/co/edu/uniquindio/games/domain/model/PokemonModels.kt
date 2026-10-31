package co.edu.uniquindio.games.domain.model

data class PokemonItem(
    val id: Int,
    val name: String,
    val imageUrl: String
)

data class PokemonStat(
    val name: String,
    val value: Int
)

data class PokemonDetail(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val heightInMeters: Double,
    val weightInKg: Double,
    val types: List<String>,
    val abilities: List<String>,
    val stats: List<PokemonStat>,
    val description: String,
    val habitat: String,
    val generation: String
)