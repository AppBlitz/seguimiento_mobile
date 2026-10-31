package co.edu.uniquindio.games.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.edu.uniquindio.games.ui.pokemon_detail.PokemonDetailScreen
import co.edu.uniquindio.games.ui.pokemon_detail.PokemonDetailViewModel
import co.edu.uniquindio.games.ui.pokemon_list.PokemonListScreen
import co.edu.uniquindio.games.ui.pokemon_list.PokemonListViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "pokemon_list"
    ) {
        composable("pokemon_list") {
            val viewModel: PokemonListViewModel = viewModel(
                factory = PokemonListViewModel.Factory
            )
            PokemonListScreen(
                viewModel = viewModel,
                onPokemonClick = { pokemonId ->
                    navController.navigate("pokemon_detail/$pokemonId")
                }
            )
        }

        composable(
            route = "pokemon_detail/{pokemonId}",
            arguments = listOf(
                navArgument("pokemonId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val pokemonId = backStackEntry.arguments?.getInt("pokemonId") ?: 1
            val detailViewModel: PokemonDetailViewModel = viewModel(
                key = "detail_$pokemonId",
                factory = PokemonDetailViewModel.provideFactory(pokemonId)
            )
            PokemonDetailScreen(
                viewModel = detailViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}