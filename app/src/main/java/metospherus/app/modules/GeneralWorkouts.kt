package metospherus.app.modules

import java.net.URL

data class GeneralWorkouts(
    val name: String,
    val description: String,
    val videosUrl: VideosUrlList,
    val categories: GeneralWorkoutsCategory,
)

data class GeneralWorkoutsCategory(
    val id: Long = 0,
    val name: String? = null,
    val image: String? = null,
)

data class VideosUrlList(
    val url: URL,
    val title: String
)