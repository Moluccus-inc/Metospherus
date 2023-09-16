package metospherus.app.modules

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST

interface FCMService {
    @Headers(
        "Content-Type: application/json",
        "Authorization: key=AAAAnB858Xs:APA91bG1VtuWm1hZ8VDE85jy9pdaWiHG_XIXJ8ISqQPN_TRd0bpLI72bqpDgYtufEsEEvUjigNsr4wUPGRtNZz2JZQ1MY0g2G1XxW2o454ttGwxOx09Xemq_KxgE0ixGHCTVrE8KB4Ch"
    )
    @POST("fcm/send")
    fun sendNotification(
        @HeaderMap headers: Map<String, String>,
        @Body notification: FCMNotification
    ): Call<FCMResponse>
}