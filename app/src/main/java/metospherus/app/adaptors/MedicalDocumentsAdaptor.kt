package metospherus.app.adaptors

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import metospherus.app.R
import metospherus.app.modules.GeneralDocuments
import metospherus.app.modules.GeneralTemplate

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
            itemView.findViewById<TextView>(R.id.documentShortDescription).text = generalDocs.documentShortDescription
            itemView.findViewById<Chip>(R.id.documentDate).text = generalDocs.documentDate
            itemView.findViewById<Chip>(R.id.documentSyncStatus).text = generalDocs.documentSyncStatus

            itemView.findViewById<LinearLayout>(R.id.documentCardContainer).setOnClickListener {
                // click to view document
            }

            Glide.with(context)
                .load(generalDocs.documentPreview)
                .centerCrop()
                .into(itemView.findViewById(R.id.documentPreview))
        }
    }
}