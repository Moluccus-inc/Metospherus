package metospherus.app.utilities

import android.content.Context
import android.view.Gravity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onPreShow
import com.afollestad.materialdialogs.customview.customView
import com.google.android.material.textfield.TextInputEditText
import metospherus.app.R
import metospherus.app.utilities.Constructor.createBugReport
import metospherus.app.utilities.Constructor.sendEmail

fun requestFeatureOrReportBugs(context: Context) {
    MaterialDialog(context).show {
        customView(R.layout.feedback_request_layout)
        cornerRadius(literalDp = 20f)
        onPreShow {
            val displayMetrics = windowContext.resources.displayMetrics
            val dialogWidth =
                displayMetrics.widthPixels - (2 * windowContext.resources.getDimensionPixelSize(
                    R.dimen.dialog_margin_horizontal
                ))

            // Set the dialog width
            val layoutParams = window?.attributes
            layoutParams?.width = dialogWidth

            // Set the dialog position to 5dp from the bottom
            layoutParams?.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            layoutParams?.y = Constructor.dpToPx(10) // Adjust this value as needed
            window?.attributes = layoutParams
        }

        val title = view.findViewById<TextInputEditText>(R.id.titleDiscussion)
        val discussionMses = view.findViewById<TextInputEditText>(R.id.discussionMsg)

        negativeButton(text = "cancel") {
            dismiss()
        }

        positiveButton(text = "commit message") {
            if (title.text!!.isNotEmpty() && discussionMses.text!!.isNotEmpty()) {
                sendEmail(
                    title.text!!.trim().toString(),
                    discussionMses.text!!.trim().toString(),
                    "la.niina.me@gmail.com",
                    context
                )
            }
        }
    }
}