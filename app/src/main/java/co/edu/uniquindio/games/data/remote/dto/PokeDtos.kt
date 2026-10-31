package co.edu.uniquindio.games.data.remote.dto

import co.edu.uniquindio.games.domain.model.PokemonDetail
import co.edu.uniquindio.games.domain.model.PokemonItem
import co.edu.uniquindio.games.domain.model.PokemonStat
import com.google.gson.annotations.SerializedName

data class PokemonListResponseDto(
    @SerializedName("count") val count: Int = 0,
    @SerializedName("results") val results: List<NamedApiResourceDto> = emptyList()
)

data class NamedApiResourceDto(
    @SerializedName("name") val name: String = "",
    @SerializedName("url") val url: String = ""
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

data class TypeResponseDto(
    @SerializedName("pokemon") val pokemon: List<TypePokemonEntryDto> = emptyList()
)

data class TypePokemonEntryDto(
    @SerializedName("pokemon") val pokemon: NamedApiResourceDto
)

data class HabitatResponseDto(
    @SerializedName("pokemon_species") val pokemonSpecies: List<NamedApiResourceDto> = emptyList()
)

data class PokemonDetailDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("height") val height: Int = 0,
    @SerializedName("weight") val weight: Int = 0,
    @SerializedName("types") val types: List<PokemonTypeSlotDto> = emptyList(),
    @SerializedName("abilities") val abilities: List<PokemonAbilitySlotDto> = emptyList(),
    @SerializedName("stats") val stats: List<PokemonStatSlotDto> = emptyList()
) {
    fun toDomainItem(): PokemonItem = PokemonItem(
        id = id,
        name = name.replaceFirstChar { it.uppercase() },
        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
    )
}

data class PokemonTypeSlotDto(
    @SerializedName("type") val type: NamedApiResourceDto
)

data class PokemonAbilitySlotDto(
    @SerializedName("ability") val ability: NamedApiResourceDto
)

data class PokemonStatSlotDto(
    @SerializedName("base_stat") val baseStat: Int = 0,
    @SerializedName("stat") val stat: NamedApiResourceDto
)

data class PokemonSpeciesDto(
    @SerializedName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntryDto> = emptyList(),
    @SerializedName("habitat") val habitat: NamedApiResourceDto? = null,
    @SerializedName("generation") val generation: NamedApiResourceDto? = null
)

data class FlavorTextEntryDto(
    @SerializedName("flavor_text") val flavorText: String = "",
    @SerializedName("language") val language: NamedApiResourceDto
)

fun mapToPokemonDetail(
    detail: PokemonDetailDto,
    species: PokemonSpeciesDto?
): PokemonDetail {
    val spanishFlavor = species?.flavorTextEntries
        ?.firstOrNull { it.language.name == "es" }
        ?.flavorText
    val englishFlavor = species?.flavorTextEntries
        ?.firstOrNull { it.language.name == "en" }
        ?.flavorText
    val cleanDescription = (spanishFlavor ?: englishFlavor ?: "Sin descripción disponible.")
        .replace("\n", " ")
        .replace("\u000c", " ")

    return PokemonDetail(
        id = detail.id,
        name = detail.name.replaceFirstChar { it.uppercase() },
        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${detail.id}.png",
        heightInMeters = detail.height / 10.0,
        weightInKg = detail.weight / 10.0,
        types = detail.types.map { it.type.name.replaceFirstChar { c -> c.uppercase() } },
        abilities = detail.abilities.map { it.ability.name.replaceFirstChar { c -> c.uppercase() } },
        stats = detail.stats.map {
            PokemonStat(
                name = it.stat.name.uppercase(),
                value = it.baseStat
            )
        },
        description = cleanDescription,
        habitat = species?.habitat?.name?.replaceFirstChar { it.uppercase() } ?: "Desconocido",
        generation = species?.generation?.name?.uppercase() ?: "N/A"
    )
}