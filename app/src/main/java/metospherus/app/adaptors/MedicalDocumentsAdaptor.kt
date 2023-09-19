package metospherus.app.adaptors

import android.annotation.SuppressLint
import android.content.Context
import android.text.Layout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AlignmentSpan
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onPreShow
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import metospherus.app.R
import metospherus.app.modules.FormattingInfo
import metospherus.app.modules.GeneralDocuments
import metospherus.app.modules.GeneralTemplate
import metospherus.app.utilities.Constructor
import metospherus.app.utilities.Constructor.hide
import metospherus.app.utilities.Constructor.show
import metospherus.app.utilities.MoluccusToast

class MedicalDocumentsAdaptor(private val context: Context) :
    RecyclerView.Adapter<MedicalDocumentsAdaptor.ViewHolder>() {
    private val serviceList: MutableList<GeneralDocuments> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(trackerInstance: MutableList<GeneralDocuments>) {
        serviceList.clear()
        serviceList.addAll(trackerInstance)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.medical_document_cards, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(serviceList[position], context)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("NewApi")
        fun bind(generalDocs: GeneralDocuments, context: Context) {
            val formattedText = generalDocs.formattedText
            val formattingInfoListJson = generalDocs.formattingInfoList
            if (formattingInfoListJson != null) {
                val gson = Gson()
                val formattingInfoListType = object : TypeToken<List<FormattingInfo>>() {}.type
                val formattingInfoList = gson.fromJson<List<FormattingInfo>>(formattingInfoListJson, formattingInfoListType)

                val spannable = SpannableStringBuilder(formattedText)
                for (formattingInfo in formattingInfoList) {
                    val styleSpan = StyleSpan(formattingInfo.style)
                    spannable.setSpan(styleSpan, formattingInfo.start, formattingInfo.end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                }

                itemView.findViewById<TextView>(R.id.textViewDocuments).text = spannable
            }
            itemView.findViewById<TextView>(R.id.timeStamp).text = generalDocs.time

            itemView.findViewById<MaterialCardView>(R.id.documentCardContainer).setOnClickListener {
                MaterialDialog(context).show {
                    customView(R.layout.preview_medical_documents)
                    cornerRadius(literalDp = 20f)

                    val addOrSaveDocument = view.findViewById<FloatingActionButton>(R.id.addOrSaveDocument)
                    val documentInputEditText = view.findViewById<TextInputEditText>(R.id.documentInputEditText)
                    val markSelectedTextBold = findViewById<FloatingActionButton>(R.id.markSelectedTextBold)
                    val markSelectedTextItalic = findViewById<FloatingActionButton>(R.id.markSelectedTextItalic)
                    val addBulletDotsTo = findViewById<FloatingActionButton>(R.id.addBulletDotsTo)
                    val alignSelectedTextToTextStart = findViewById<FloatingActionButton>(R.id.alignSelectedTextToTextStart)
                    val alignSelectedTextToTextCenter = findViewById<FloatingActionButton>(R.id.alignSelectedTextToTextCenter)
                    val alignSelectedTextToTextEnd = findViewById<FloatingActionButton>(R.id.alignSelectedTextToTextEnd)

                    documentInputEditText.isEnabled = false
                    if (formattingInfoListJson != null) {
                        val gson = Gson()
                        val formattingInfoListType = object : TypeToken<List<FormattingInfo>>() {}.type
                        val formattingInfoList = gson.fromJson<List<FormattingInfo>>(formattingInfoListJson, formattingInfoListType)

                        val spannable = SpannableStringBuilder(formattedText)
                        for (formattingInfo in formattingInfoList) {
                            val styleSpan = StyleSpan(formattingInfo.style)
                            spannable.setSpan(styleSpan, formattingInfo.start, formattingInfo.end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                        }

                        documentInputEditText.text = spannable
                    }

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
                }
            }
        }
    }
}