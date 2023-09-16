package metospherus.app.modules

data class FCMNotification(
    val to: String? = null,
    val data: FCMNotificationData
)

data class FCMNotificationData(
    val title: String? = null,
    val body: String? = null,
    val imageUrl: String? = null,
    val otherUserUid: String? = null,
)

data class FCMResponse(
    val message_id: String? = null,
)
