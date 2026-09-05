package com.ndichan.app.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.ndichan.app.core.components.NDiChanBottomBar
import com.ndichan.app.core.theme.BgPrimary
import com.ndichan.app.presentation.anime.AnimeBrowseScreen
import com.ndichan.app.presentation.anime.detail.AnimeDetailScreen
import com.ndichan.app.presentation.anime.player.VideoPlayerScreen
import com.ndichan.app.presentation.home.HomeScreen
import com.ndichan.app.presentation.library.LibraryScreen
import com.ndichan.app.presentation.manga.MangaBrowseScreen
import com.ndichan.app.presentation.manga.detail.MangaDetailScreen
import com.ndichan.app.presentation.manga.reader.MangaReaderScreen
import com.ndichan.app.presentation.search.SearchScreen
import com.ndichan.app.presentation.settings.SettingsScreen

@Composable
fun NDiChanNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hide bottom bar on fullscreen player and reader screens
    val hideBottomBar = currentRoute?.startsWith("video_player") == true ||
            currentRoute?.startsWith("manga_reader") == true

    Scaffold(
        containerColor = BgPrimary,
        bottomBar = {
            if (!hideBottomBar) {
                NDiChanBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.fillMaxSize(),
            enterTransition = {
                fadeIn(animationSpec = tween(220)) + slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    tween(220)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(200))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(220))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(200)) + slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    tween(200)
                )
            }
        ) {
            // Home
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToAnimeDetail = { animeId ->
                        navController.navigate(Screen.AnimeDetail.createRoute(animeId))
                    },
                    onNavigateToMangaDetail = { mangaSlug ->
                        navController.navigate(Screen.MangaDetail.createRoute(mangaSlug))
                    },
                    onNavigateToVideoPlayer = { episodeId, animeId, episodeTitle ->
                        navController.navigate(
                            Screen.VideoPlayer.createRoute(episodeId, animeId, episodeTitle)
                        )
                    },
                    onNavigateToMangaReader = { chapterSlug, mangaSlug ->
                        navController.navigate(
                            Screen.MangaReader.createRoute(chapterSlug, mangaSlug)
                        )
                    },
                    onNavigateToSearch = {
                        navController.navigate(Screen.Search.route)
                    },
                    onNavigateToAnimeBrowse = {
                        navController.navigate(Screen.AnimeBrowse.route)
                    },
                    onNavigateToMangaBrowse = {
                        navController.navigate(Screen.MangaBrowse.route)
                    }
                )
            }

            // Anime Browse
            composable(Screen.AnimeBrowse.route) {
                AnimeBrowseScreen(
                    onNavigateToDetail = { animeId ->
                        navController.navigate(Screen.AnimeDetail.createRoute(animeId))
                    },
                    onNavigateToSearch = {
                        navController.navigate(Screen.Search.route)
                    }
                )
            }

            // Manga Browse
            composable(Screen.MangaBrowse.route) {
                MangaBrowseScreen(
                    onNavigateToDetail = { mangaSlug ->
                        navController.navigate(Screen.MangaDetail.createRoute(mangaSlug))
                    },
                    onNavigateToSearch = {
                        navController.navigate(Screen.Search.route)
                    }
                )
            }

            // Library
            composable(Screen.Library.route) {
                LibraryScreen(
                    onNavigateToAnimeDetail = { animeId ->
                        navController.navigate(Screen.AnimeDetail.createRoute(animeId))
                    },
                    onNavigateToMangaDetail = { mangaSlug ->
                        navController.navigate(Screen.MangaDetail.createRoute(mangaSlug))
                    },
                    onNavigateToVideoPlayer = { episodeId, animeId, episodeTitle ->
                        navController.navigate(
                            Screen.VideoPlayer.createRoute(episodeId, animeId, episodeTitle)
                        )
                    },
                    onNavigateToMangaReader = { chapterSlug, mangaSlug ->
                        navController.navigate(
                            Screen.MangaReader.createRoute(chapterSlug, mangaSlug)
                        )
                    }
                )
            }

            // Settings
            composable(Screen.Settings.route) {
                SettingsScreen()
            }

            // Search
            composable(Screen.Search.route) {
                SearchScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAnimeDetail = { animeId ->
                        navController.navigate(Screen.AnimeDetail.createRoute(animeId))
                    },
                    onNavigateToMangaDetail = { mangaSlug ->
                        navController.navigate(Screen.MangaDetail.createRoute(mangaSlug))
                    }
                )
            }

            // Anime Detail
            composable(
                route = Screen.AnimeDetail.route,
                arguments = listOf(
                    navArgument("animeId") { type = NavType.StringType }
                )
            ) {
                AnimeDetailScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPlayer = { episodeId, animeId, episodeTitle ->
                        navController.navigate(
                            Screen.VideoPlayer.createRoute(episodeId, animeId, episodeTitle)
                        )
                    }
                )
            }

            // Video Player
            composable(
                route = Screen.VideoPlayer.route,
                arguments = listOf(
                    navArgument("episodeId") { type = NavType.StringType },
                    navArgument("animeId") {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                    navArgument("episodeTitle") {
                        type = NavType.StringType
                        defaultValue = "Episode"
                    }
                )
            ) {
                VideoPlayerScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToNextEpisode = { nextEpisodeId, animeId, title ->
                        navController.navigate(
                            Screen.VideoPlayer.createRoute(nextEpisodeId, animeId, title)
                        ) {
                            popUpTo(Screen.VideoPlayer.route) { inclusive = true }
                        }
                    }
                )
            }

            // Manga Detail
            composable(
                route = Screen.MangaDetail.route,
                arguments = listOf(
                    navArgument("mangaSlug") { type = NavType.StringType }
                )
            ) {
                MangaDetailScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToReader = { chapterSlug, mangaSlug ->
                        navController.navigate(
                            Screen.MangaReader.createRoute(chapterSlug, mangaSlug)
                        )
                    },
                    onNavigateToOtherManga = { slug ->
                        navController.navigate(Screen.MangaDetail.createRoute(slug))
                    }
                )
            }

            // Manga Reader
            composable(
                route = Screen.MangaReader.route,
                arguments = listOf(
                    navArgument("chapterSlug") { type = NavType.StringType },
                    navArgument("mangaSlug") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) {
                MangaReaderScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToChapter = { nextChapterSlug, mangaSlug ->
                        navController.navigate(
                            Screen.MangaReader.createRoute(nextChapterSlug, mangaSlug)
                        ) {
                            popUpTo(Screen.MangaReader.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
