package metospherus.app.adaptors

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.imageview.ShapeableImageView
import metospherus.app.R
import metospherus.app.modules.GeneralDocuments
import metospherus.app.modules.GeneralTemplate
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
            itemView.findViewById<TextView>(R.id.documentTitle).text = generalDocs.documentTitle
            itemView.findViewById<TextView>(R.id.documentShortDescription).text =
                generalDocs.documentShortDescription
            itemView.findViewById<TextView>(R.id.documentDate).text = generalDocs.documentDate
            itemView.findViewById<Chip>(R.id.documentSyncStatus).text =
                generalDocs.documentSyncStatus

            itemView.findViewById<LinearLayout>(R.id.documentCardContainer).setOnClickListener {
                MaterialDialog(context).show {
                    customView(R.layout.preview_medical_documents)
                    cornerRadius(literalDp = 20f)

                    val closeBottomSheet = view.findViewById<ImageView>(R.id.closeBottomSheet)
                    val documentTitleHolder = view.findViewById<TextView>(R.id.documentTitleHolder)
                    val documentDescription = view.findViewById<TextView>(R.id.documentDescription)
                    val documentImageHolder = view.findViewById<ShapeableImageView>(R.id.documentImageHolder)
                    val documentsNotesHolder = view.findViewById<TextView>(R.id.documentsNotesHolder)
                    val documentTimeout = view.findViewById<TextView>(R.id.documentTimeout)

                    documentTitleHolder.text = generalDocs.documentTitle
                    documentDescription.text = generalDocs.documentShortDescription
                    documentsNotesHolder.text = generalDocs.documentNotes
                    documentTimeout.text = generalDocs.documentDate

                    if (generalDocs.documentPreview != null) {
                        documentImageHolder.show()
                        Glide.with(context)
                            .load(generalDocs.documentPreview)
                            .centerCrop()
                            .into(documentImageHolder)
                    } else {
                        documentImageHolder.hide()
                    }

                    closeBottomSheet.setOnClickListener {
                        dismiss()
                    }

                    onShow {
                        val displayMetrics = windowContext.resources.displayMetrics
                        val dialogWidth =
                            displayMetrics.widthPixels - (2 * windowContext.resources.getDimensionPixelSize(
                                R.dimen.dialog_margin_horizontal
                            ))
                        window?.setLayout(
                            dialogWidth,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    }
                }
            }

            Glide.with(context)
                .load(generalDocs.documentPreview)
                .centerCrop()
                .into(itemView.findViewById(R.id.documentPreview))
        }
    }
}