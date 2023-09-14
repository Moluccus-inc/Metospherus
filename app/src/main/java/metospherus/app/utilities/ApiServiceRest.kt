package metospherus.app.utilities

import okhttp3.Call
import retrofit2.http.GET

interface ApiServiceRest {
    @GET("posts/1")
    fun getPost(): Call
}