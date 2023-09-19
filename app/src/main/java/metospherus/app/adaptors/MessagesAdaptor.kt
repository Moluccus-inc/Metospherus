package metospherus.app.adaptors

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import metospherus.app.R
import metospherus.app.modules.GeneralMessages

class MessagesAdaptor(
    private val context: Context,
    private val auth: FirebaseAuth,
    private val otheruseruid: String?,
    private val db: FirebaseDatabase
) : RecyclerView.Adapter<MessagesAdaptor.ViewHolder>() {
    private val serviceList: MutableList<GeneralMessages> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(msg: MutableList<GeneralMessages>) {
        serviceList.clear()
        serviceList.addAll(msg)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("NewApi")
        fun bind(msg: GeneralMessages, context: Context) {
            val currentUserTimeStamp = itemView.findViewById<TextView>(R.id.currentUserTimeStamp)
            val currentUserMessage = itemView.findViewById<TextView>(R.id.messageCurrentUser)

            val otherUserTimeStamp = itemView.findViewById<TextView>(R.id.otherUsersTimeStamp)
            val otherUserMessage = itemView.findViewById<TextView>(R.id.otherUserMassage)

            val currentUserChatLayout = itemView.findViewById<LinearLayoutCompat>(R.id.currentUserSenderLayout)
            val otherUserChatLayout = itemView.findViewById<LinearLayoutCompat>(R.id.otherSenderUidLayout)

            val currentUserViewedStatus = itemView.findViewById<TextView>(R.id.currentUserViewedStatus)
            val otherUserViewedStatus = itemView.findViewById<TextView>(R.id.otherUserViewedStatus)

            if (msg.senderUid.equals(auth.currentUser?.uid.toString())){
                currentUserChatLayout.visibility = View.VISIBLE
                otherUserChatLayout.visibility = View.GONE

                currentUserViewedStatus.text = msg.viewed.toString()

                currentUserTimeStamp.text = msg.timestamp
                currentUserMessage.movementMethod = LinkMovementMethod.getInstance()

                val messageText = msg.message
                // Create a SpannableString from the message text
                val spannableMessage = SpannableString(messageText)

                // Use regex to find URLs in the message text and make them clickable
                val urlPattern = Patterns.WEB_URL
                val matcher = messageText?.let { urlPattern.matcher(it) }
                if (matcher != null) {
                    while (matcher.find()) {
                        val start = matcher.start()
                        val end = matcher.end()
                        val url = messageText.substring(start, end)
                        // Create a ClickableSpan for the URL
                        val clickableSpan = object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                // Handle clicking the link here, e.g., open the URL in a browser
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }

                            override fun updateDrawState(ds: TextPaint) {
                                super.updateDrawState(ds)
                                // Customize the appearance of the clickable link text
                                ds.isUnderlineText = true // Underline the link
                                ds.color = ContextCompat.getColor(context, koleton.base.R.color.accent_material_dark) // Set link color
                            }
                        }

                        // Set the ClickableSpan for the URL in the SpannableString
                        spannableMessage.setSpan(
                            clickableSpan,
                            start,
                            end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
                // Set the SpannableString in the TextView
                currentUserMessage.text = spannableMessage

            } else if (msg.senderUid.equals(otheruseruid)){
                otherUserChatLayout.visibility = View.VISIBLE
                currentUserChatLayout.visibility = View.GONE

                otherUserViewedStatus.text = msg.viewed
                otherUserTimeStamp.text = msg.timestamp

                val messageText = msg.message
                val spannableMessage = SpannableString(messageText)
                val urlPattern = Patterns.WEB_URL
                val matcher = messageText?.let { urlPattern.matcher(it) }
                if (matcher != null) {
                    while (matcher.find()) {
                        val start = matcher.start()
                        val end = matcher.end()
                        val url = messageText.substring(start, end)
                        // Create a ClickableSpan for the URL
                        val clickableSpan = object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                // Handle clicking the link here, e.g., open the URL in a browser
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }

                            override fun updateDrawState(ds: TextPaint) {
                                super.updateDrawState(ds)
                                // Customize the appearance of the clickable link text
                                ds.isUnderlineText = true // Underline the link
                                ds.color = ContextCompat.getColor(context, koleton.base.R.color.accent_material_dark) // Set link color
                            }
                        }

                        // Set the ClickableSpan for the URL in the SpannableString
                        spannableMessage.setSpan(
                            clickableSpan,
                            start,
                            end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }

                otherUserMessage.text = spannableMessage
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.metospherus_chat_design, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(serviceList[position], context)
    }
}