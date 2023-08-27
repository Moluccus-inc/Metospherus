package metospherus.app.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import metospherus.app.R

@Suppress("DEPRECATION")
class MoluccusToast(private val context: Context) {

    private val toastQueue: MutableList<Toast> = mutableListOf()

    @SuppressLint("InflateParams")
    private fun showToast(
        title: String,
        message: String,
        iconResId: Drawable
    ) {
        val toastView = LayoutInflater.from(context).inflate(R.layout.layout_moluccus_toast, null)

        toastView.findViewById<TextView>(R.id.toastMessage).text = message
        toastView.findViewById<TextView>(R.id.toastTitle).text = title
        toastView.findViewById<ImageView>(R.id.toastIcon).setImageDrawable(iconResId)

        toastView.findViewById<ImageView>(R.id.toastClose).setOnClickListener {
            toastQueue.removeAt(0).cancel()
            showNextToast()
        }

        val toast = Toast(context)

        // Set the custom view for the toast
        toast.view = toastView

        // Set the custom view's layout parameters to match parent's width
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        toastView.layoutParams = layoutParams
        toastQueue.add(toast)
        if (toastQueue.size == 1) {
            toast.show()
        }
    }

    private fun showNextToast() {
        toastQueue.removeAt(0) // Remove the current toast
        if (toastQueue.isNotEmpty()) {
            toastQueue.firstOrNull()?.show() // Show the next toast in the queue
        }
    }

    fun showSuccess(message: String) {
        showToast(
            "Success",
            message,
            AppCompatResources.getDrawable(context, R.drawable.round_verified)!!
        )
    }

    fun showInformation(message: String) {
        showToast(
            "Information",
            message,
            AppCompatResources.getDrawable(context, R.drawable.round_info)!!
        )
    }

    fun showError(message: String) {
        showToast(
            "Error",
            message,
            AppCompatResources.getDrawable(context, R.drawable.round_error)!!
        )
    }
}