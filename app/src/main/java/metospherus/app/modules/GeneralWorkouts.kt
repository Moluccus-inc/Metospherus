package metospherus.app.modules

import java.net.URL

data class GeneralWorkouts(
    val name: String,
    val description: String,
    val videosUrl: VideosUrlList
)

data class VideosUrlList(
    val url: URL,
    val title: String
)