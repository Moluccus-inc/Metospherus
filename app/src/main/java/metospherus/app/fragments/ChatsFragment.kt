package metospherus.app.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import metospherus.app.R
import metospherus.app.adaptors.MessagesAdaptor
import metospherus.app.database.profile_data.GeneralUserInformation
import metospherus.app.databinding.MetospherusLayoutChatBinding
import metospherus.app.modules.FCMNotification
import metospherus.app.modules.FCMNotificationData
import metospherus.app.modules.FCMResponse
import metospherus.app.modules.GeneralMessages
import metospherus.app.modules.RetrofitClient
import metospherus.app.utilities.FirebaseConfig.retrieveRealtimeDatabaseOnListener
import metospherus.app.utilities.MoluccusToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ChatsFragment : Fragment() {
    private var _binding: MetospherusLayoutChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private var otheruseruidString = ""

    private lateinit var recyclerViewMessages: RecyclerView
    private lateinit var messagesAdaptor: MessagesAdaptor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MetospherusLayoutChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        auth.useAppLanguage()
        db = FirebaseDatabase.getInstance()

        otheruseruidString = arguments?.getString("otheruid").toString()

        messagesAdaptor = MessagesAdaptor(requireContext(), auth, otheruseruidString, db)
        recyclerViewMessages = binding.chatsRecyclerView
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerViewMessages.layoutManager = linearLayoutManager
        val latestMessagePosition = messagesAdaptor.itemCount - 1
        recyclerViewMessages.adapter = messagesAdaptor
        if (latestMessagePosition >= 0) {
            recyclerViewMessages.scrollToPosition(latestMessagePosition)
        }

        binding.senderMessage.isEnabled = false
        binding.inputMessage.doAfterTextChanged {
            it?.let {
                binding.senderMessage.isEnabled = it.length >= 4
            }
        }

        binding.senderMessage.setOnClickListener {
            if (!TextUtils.isEmpty(binding.inputMessage.text)) {
                sendMessageAndNotifyUser(binding.inputMessage.text.toString(), binding.inputMessage, otheruseruidString)
            }
        }

        val toolbarLayout = binding.toolbarLayout
        val customToolbar = LayoutInflater.from(requireContext()).inflate(R.layout.custom_toolbar, null)
        toolbarLayout.addView(customToolbar)

        val profileImage = customToolbar.findViewById<ImageView>(R.id.profileImage)
        val usernameText = customToolbar.findViewById<TextView>(R.id.usernameText)
        val userProfileText = customToolbar.findViewById<TextView>(R.id.userProfileText)
        customToolbar.findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            findNavController().navigate(R.id.action_chatsFragment_to_HomeFragment)
        }

        retrieveRealtimeDatabaseOnListener(
            db, "participants/$otheruseruidString",
            requireContext(),
            onDataChange = { valueDataSnap ->
                if (valueDataSnap.exists()) {
                    val profilesValue = valueDataSnap.getValue(GeneralUserInformation::class.java)

                    Glide.with(requireContext())
                        .load(profilesValue?.avatar)
                        .centerCrop()
                        .placeholder(R.drawable.holder)
                        .into(profileImage)

                    if (profilesValue != null) {
                        usernameText.text = profilesValue.generalDescription.usrPreferedName
                    }
                    if (profilesValue != null) {
                        userProfileText.text = profilesValue.accountType
                    }

                }
            })

        retrieveMessages()
    }

    override fun onResume() {
        super.onResume()
        retrieveMessages()
    }

    override fun onPause() {
        super.onPause()
        retrieveMessages()
    }

    private fun retrieveMessages() {
        auth.currentUser?.uid?.let { currentUserId ->
            val liveMessagesData = mutableListOf<GeneralMessages>()

            val messageKey = if (currentUserId < otheruseruidString) {
                "${currentUserId}•$otheruseruidString"
            } else {
                "$otheruseruidString•${currentUserId}"
            }
            val messageReference = db.getReference("MedicalMessenger").child(messageKey)
            messageReference.addChildEventListener(object : ChildEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(GeneralMessages::class.java)
                    if (message != null) {
                        liveMessagesData.add(message)
                        val customComparator = Comparator<GeneralMessages> { message1, message2 ->
                            val dateFormat = SimpleDateFormat("MMMM d, yyyy h:mm:ss a", Locale.US)
                            val date1 = dateFormat.parse("${message1.datestamp} ${message1.timestamp}")
                            val date2 = dateFormat.parse("${message2.datestamp} ${message2.timestamp}")
                            date1!!.compareTo(date2)
                        }
                        liveMessagesData.sortWith(customComparator)
                        val latestMessagePosition = liveMessagesData.size - 1
                        messagesAdaptor.setData(liveMessagesData)
                        if (latestMessagePosition >= 0) {
                            recyclerViewMessages.scrollToPosition(latestMessagePosition)
                        }

                        if (message.senderUid != currentUserId) {
                            if (message.viewed != "✔✔") {
                                snapshot.ref.child("viewed").setValue("✔✔")
                            }
                        }
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(GeneralMessages::class.java)
                    if (message != null) {
                        liveMessagesData.add(message)
                        val customComparator = Comparator<GeneralMessages> { message1, message2 ->
                            val dateFormat = SimpleDateFormat("MMMM d, yyyy h:mm:ss a", Locale.US)
                            val date1 = dateFormat.parse("${message1.datestamp} ${message1.timestamp}")
                            val date2 = dateFormat.parse("${message2.datestamp} ${message2.timestamp}")
                            date1!!.compareTo(date2)
                        }
                        liveMessagesData.sortWith(customComparator)
                        val latestMessagePosition = liveMessagesData.size - 1
                        messagesAdaptor.setData(liveMessagesData)
                        if (latestMessagePosition >= 0) {
                            recyclerViewMessages.scrollToPosition(latestMessagePosition)
                        }

                        if (message.senderUid != currentUserId) {
                            if (message.viewed != "✔✔") {
                                snapshot.ref.child("viewed").setValue("✔✔")
                            }
                        }
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val message = snapshot.getValue(GeneralMessages::class.java)
                    if (message != null) {
                        liveMessagesData.add(message)
                        val customComparator = Comparator<GeneralMessages> { message1, message2 ->
                            val dateFormat = SimpleDateFormat("MMMM d, yyyy h:mm:ss a", Locale.US)
                            val date1 = dateFormat.parse("${message1.datestamp} ${message1.timestamp}")
                            val date2 = dateFormat.parse("${message2.datestamp} ${message2.timestamp}")
                            date1!!.compareTo(date2)
                        }
                        liveMessagesData.sortWith(customComparator)
                        val latestMessagePosition = liveMessagesData.size - 1
                        messagesAdaptor.setData(liveMessagesData)
                        if (latestMessagePosition >= 0) {
                            recyclerViewMessages.scrollToPosition(latestMessagePosition)
                        }

                        if (message.senderUid != currentUserId) {
                            if (message.viewed != "✔✔") {
                                snapshot.ref.child("viewed").setValue("✔✔")
                            }
                        }
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(GeneralMessages::class.java)
                    if (message != null) {
                        liveMessagesData.add(message)
                        val customComparator = Comparator<GeneralMessages> { message1, message2 ->
                            val dateFormat = SimpleDateFormat("MMMM d, yyyy h:mm:ss a", Locale.US)
                            val date1 = dateFormat.parse("${message1.datestamp} ${message1.timestamp}")
                            val date2 = dateFormat.parse("${message2.datestamp} ${message2.timestamp}")
                            date1!!.compareTo(date2)
                        }
                        liveMessagesData.sortWith(customComparator)
                        val latestMessagePosition = liveMessagesData.size - 1
                        messagesAdaptor.setData(liveMessagesData)
                        if (latestMessagePosition >= 0) {
                            recyclerViewMessages.scrollToPosition(latestMessagePosition)
                        }

                        if (message.senderUid != currentUserId) {
                            if (message.viewed != "✔✔") {
                                snapshot.ref.child("viewed").setValue("✔✔")
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    MoluccusToast(requireContext()).showError(error.message)
                }

            })
        }
    }


    @SuppressLint("SimpleDateFormat")
    private fun sendMessageAndNotifyUser(
        message: String,
        inputMessage: TextInputEditText,
        otheruseruid: String
    ) {
        val calendar = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("h:mm:ss a")
        val formattedTime = timeFormat.format(calendar.time)

        val dateFormat = SimpleDateFormat("MMMM d, yyyy")
        val formattedDate = dateFormat.format(calendar.time)

        val messageSender = mapOf(
            "message" to message,
            "senderUid" to auth.currentUser?.uid,
            "timestamp" to formattedTime.toString(),
            "datestamp" to formattedDate.toString(),
            "viewed" to "✔"
        )

        val messageKey = if (auth.currentUser?.uid!! < otheruseruid) {
            "${auth.currentUser?.uid}•${otheruseruid}"
        } else {
            "${otheruseruid}•${auth.currentUser?.uid}"
        }

        val messageReference = db.getReference("MedicalMessenger").child(messageKey)
        val newMessageReference = messageReference.push()
        newMessageReference.setValue(messageSender)
            .addOnSuccessListener {
                sendNotificationToOtherUser(otheruseruid, message, inputMessage)
            }
            .addOnFailureListener {
                inputMessage.error = "Error ${it.message}"
                retrieveMessages()
            }
    }

    private fun sendNotificationToOtherUser(
        otherUserUid: String,
        messageBody: String,
        inputMessage: TextInputEditText,
    ) {
        val fcmService = RetrofitClient.getFCMService()
        val headers = mapOf(
            "Content-Type" to "application/json",
            "Authorization" to "key=AAAAnB858Xs:APA91bG1VtuWm1hZ8VDE85jy9pdaWiHG_XIXJ8ISqQPN_TRd0bpLI72bqpDgYtufEsEEvUjigNsr4wUPGRtNZz2JZQ1MY0g2G1XxW2o454ttGwxOx09Xemq_KxgE0ixGHCTVrE8KB4Ch"
        )
        retrieveRealtimeDatabaseOnListener(db, "participants/${auth.currentUser?.uid}", requireContext()) { dataSnapshot ->
            val avatarValue = dataSnapshot.getValue(GeneralUserInformation::class.java)
            retrieveRealtimeDatabaseOnListener(db, "MedicalMessenger/FcmTokens", requireContext()) { dataKeySnap ->
                if (avatarValue != null) {
                    val notification = FCMNotification(
                        to = dataKeySnap.child(otherUserUid).getValue(String::class.java),
                        data = FCMNotificationData(
                            title = avatarValue.generalDescription.usrPreferedName,
                            body = messageBody,
                            imageUrl = avatarValue.avatar,
                            otherUserUid= otherUserUid
                        )
                    )

                    val call = fcmService.sendNotification(headers, notification)
                    call.enqueue(object : Callback<FCMResponse> {
                        override fun onResponse(call: Call<FCMResponse>, response: Response<FCMResponse>) {
                            if (response.isSuccessful) {
                                inputMessage.text?.clear()
                                retrieveMessages()
                            } else {
                                try {
                                    val errorBodyString = response.errorBody()?.string() ?: "Unknown error"
                                    inputMessage.error = "Error: $errorBodyString"
                                    println("FCM ERROR: $errorBodyString")
                                } catch (e: Exception) {
                                    inputMessage.error = "Error: ${e.message}"
                                    println("FCM ERROR: ${e.message}")
                                }
                                retrieveMessages()
                            }
                        }

                        override fun onFailure(call: Call<FCMResponse>, t: Throwable) {
                            inputMessage.error = "Error ${t.message}"
                            println("Error exception: ${t.message}")
                            retrieveMessages()
                        }
                    })
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}