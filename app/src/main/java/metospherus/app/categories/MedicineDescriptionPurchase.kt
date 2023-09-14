package metospherus.app.categories

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.setPeekHeight
import com.afollestad.materialdialogs.callbacks.onPreShow
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide
import metospherus.app.R
import metospherus.app.modules.GeneralPharmaciesSales
import metospherus.app.utilities.Constructor.dpToPx

fun medicineDescriptionPurchase(context: Context, pharma: GeneralPharmaciesSales) {
    MaterialDialog(context).show {
        customView(R.layout.pharmacy_medicine_description)
        cornerRadius(literalDp = 20f)
        onPreShow {
            val displayMetrics = windowContext.resources.displayMetrics
            val dialogWidth = displayMetrics.widthPixels - (2 * windowContext.resources.getDimensionPixelSize(
                R.dimen.dialog_margin_horizontal
            ))

            // Set the dialog width
            val layoutParams = window?.attributes
            layoutParams?.width = dialogWidth

            // Set the dialog position to 5dp from the bottom
            layoutParams?.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            layoutParams?.y = dpToPx(10) // Adjust this value as needed

            window?.attributes = layoutParams
        }


        val titleDialog = view.findViewById<TextView>(R.id.titleDialog)
        val descriptionHolderDialog = view.findViewById<TextView>(R.id.descriptionHolderDialog)
        val medicineImage = view.findViewById<ImageView>(R.id.medicineImage)

        titleDialog.text = pharma.name
        descriptionHolderDialog.text = pharma.description
        Glide.with(context)
            .load(pharma.image)
            .centerCrop()
            .into(medicineImage)

        negativeButton(text = "cancel") {
            dismiss()
        }

        positiveButton(text = "purchase @ ${pharma.price} ${pharma.currency}") {
            dismiss()
        }
    }
}