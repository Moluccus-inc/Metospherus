package metospherus.app.adaptors

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import metospherus.app.R
import metospherus.app.categories.medicineDescriptionPurchase
import metospherus.app.modules.GeneralPharmaciesSales

class PharmaciesAdaptor(
    private val context: Context,
) : RecyclerView.Adapter<PharmaciesAdaptor.ViewHolder>() {
    private val serviceList: MutableList<GeneralPharmaciesSales> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(pharma: MutableList<GeneralPharmaciesSales>) {
        serviceList.clear()
        serviceList.addAll(pharma)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(pharma: GeneralPharmaciesSales) {
            itemView.findViewById<TextView>(R.id.medicinePlaceHolderName).text = pharma.name
            itemView.findViewById<TextView>(R.id.medicinePlaceHolderDescription).text = pharma.description
            itemView.findViewById<Chip>(R.id.medicinePlaceHolderPrice).text = "${pharma.price} ${pharma.currency}"
            itemView.findViewById<Chip>(R.id.medicinePlaceHolderUnit).text = "per strip"


            Glide.with(context)
                .load(pharma.image)
                .centerCrop()
                .into(itemView.findViewById(R.id.medicinePlaceholderImage))

            itemView.findViewById<ImageView>(R.id.medicinePlaceholderImage).setOnClickListener {
                medicineDescriptionPurchase(context, pharma)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.template_medical_shopping_card, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(serviceList[position])
    }
}