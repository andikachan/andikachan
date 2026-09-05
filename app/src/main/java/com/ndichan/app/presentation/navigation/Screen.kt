package com.ndichan.app.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AnimeBrowse : Screen("anime_browse")
    object MangaBrowse : Screen("manga_browse")
    object Library : Screen("library")
    object Settings : Screen("settings")
    object Search : Screen("search")

    object AnimeDetail : Screen("anime_detail/{animeId}") {
        fun createRoute(animeId: String) = "anime_detail/$animeId"
    }

    object VideoPlayer : Screen("video_player/{episodeId}?animeId={animeId}&episodeTitle={episodeTitle}") {
        fun createRoute(episodeId: String, animeId: String = "", episodeTitle: String = ""): String {
            val encodedTitle = java.net.URLEncoder.encode(episodeTitle, "UTF-8")
            return "video_player/$episodeId?animeId=$animeId&episodeTitle=$encodedTitle"
        }
    }

    object MangaDetail : Screen("manga_detail/{mangaSlug}") {
        fun createRoute(mangaSlug: String) = "manga_detail/$mangaSlug"
    }

    object MangaReader : Screen("manga_reader/{chapterSlug}?mangaSlug={mangaSlug}") {
        fun createRoute(chapterSlug: String, mangaSlug: String = "") = "manga_reader/$chapterSlug?mangaSlug=$mangaSlug"
    }
}
