package metospherus.app.utilities

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
class WebApi {
    object RetrofitClient {
        private const val BASE_URL = "https://wger.de/api/v2/" // Replace with your API base URL
        val apiService: ApiServiceRest by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            retrofit.create(ApiServiceRest::class.java)
        }
    }
}