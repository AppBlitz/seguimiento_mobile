package co.edu.uniquindio.games.data.remote.dto

import co.edu.uniquindio.games.domain.model.PokemonDetail
import co.edu.uniquindio.games.domain.model.PokemonItem
import co.edu.uniquindio.games.domain.model.PokemonStat
import com.google.gson.annotations.SerializedName

// DTO para Endpoint 1: Lista paginada
data class PokemonListResponseDto(
    @SerializedName("count") val count: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("previous") val previous: String?,
    @SerializedName("results") val results: List<NamedApiResourceDto>
)

data class NamedApiResourceDto(
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String
) {
    fun extractId(): Int {
        return url.trimEnd('/').substringAfterLast('/').toIntOrNull() ?: 0
    }

    fun toDomainItem(): PokemonItem {
        val id = extractId()
        return PokemonItem(
            id = id,
            name = name.replaceFirstChar { it.uppercase() },
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
        )
    }
}

// DTO para Endpoint 2: Filtrado por Tipo
data class PokemonTypeResponseDto(
    @SerializedName("name") val name: String,
    @SerializedName("pokemon") val pokemon: List<TypePokemonSlotDto>
)

data class TypePokemonSlotDto(
    @SerializedName("pokemon") val pokemon: NamedApiResourceDto
)

// DTO para Endpoint 3: Filtrado por Hábitat
data class PokemonHabitatResponseDto(
    @SerializedName("name") val name: String,
    @SerializedName("pokemon_species") val pokemonSpecies: List<NamedApiResourceDto>
)

// DTO para Endpoint 4: Detalle del Pokémon
data class PokemonDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("height") val height: Int,
    @SerializedName("weight") val weight: Int,
    @SerializedName("types") val types: List<PokemonTypeSlotDto>,
    @SerializedName("abilities") val abilities: List<PokemonAbilitySlotDto>,
    @SerializedName("stats") val stats: List<PokemonStatDto>
) {
    fun toDomainItem(): PokemonItem {
        return PokemonItem(
            id = id,
            name = name.replaceFirstChar { it.uppercase() },
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
        )
    }
}

data class PokemonTypeSlotDto(
    @SerializedName("slot") val slot: Int,
    @SerializedName("type") val type: NamedApiResourceDto
)

data class PokemonAbilitySlotDto(
    @SerializedName("ability") val ability: NamedApiResourceDto
)

data class PokemonStatDto(
    @SerializedName("base_stat") val baseStat: Int,
    @SerializedName("stat") val stat: NamedApiResourceDto
)

// DTO para Endpoint 5: Especie del Pokémon
data class PokemonSpeciesDto(
    @SerializedName("is_legendary") val isLegendary: Boolean,
    @SerializedName("habitat") val habitat: NamedApiResourceDto?,
    @SerializedName("generation") val generation: NamedApiResourceDto?,
    @SerializedName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntryDto>
)

data class FlavorTextEntryDto(
    @SerializedName("flavor_text") val flavorText: String,
    @SerializedName("language") val language: NamedApiResourceDto
)

fun mapToPokemonDetail(
    detailDto: PokemonDetailDto,
    speciesDto: PokemonSpeciesDto?
): PokemonDetail {
    val spanishDescription = speciesDto?.flavorTextEntries
        ?.firstOrNull { it.language.name == "es" }
        ?.flavorText
        ?: speciesDto?.flavorTextEntries
            ?.firstOrNull { it.language.name == "en" }
            ?.flavorText
        ?: "Sin descripción disponible en la Pokédex."

    val cleanDescription = spanishDescription
        .replace("\n", " ")
        .replace("\u000c", " ")
        .trim()

    val statTranslations = mapOf(
        "hp" to "PS (Salud)",
        "attack" to "Ataque",
        "defense" to "Defensa",
        "special-attack" to "Ataque Esp.",
        "special-defense" to "Defensa Esp.",
        "speed" to "Velocidad"
    )

    return PokemonDetail(
        id = detailDto.id,
        name = detailDto.name.replaceFirstChar { it.uppercase() },
        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${detailDto.id}.png",
        heightInMeters = detailDto.height / 10.0,
        weightInKg = detailDto.weight / 10.0,
        types = detailDto.types.map { it.type.name.replaceFirstChar { c -> c.uppercase() } },
        abilities = detailDto.abilities.map { it.ability.name.replaceFirstChar { c -> c.uppercase() } },
        stats = detailDto.stats.map {
            PokemonStat(
                name = statTranslations[it.stat.name] ?: it.stat.name,
                value = it.baseStat
            )
        },
        description = cleanDescription,
        habitat = speciesDto?.habitat?.name?.replaceFirstChar { it.uppercase() } ?: "Desconocido",
        generation = speciesDto?.generation?.name?.uppercase() ?: "N/A",
        isLegendary = speciesDto?.isLegendary ?: false
    )
}
