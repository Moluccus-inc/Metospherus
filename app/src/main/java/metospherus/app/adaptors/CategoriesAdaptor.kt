package metospherus.app.adaptors

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import metospherus.app.R
import metospherus.app.modules.GeneralCategory
import metospherus.app.utilities.InitializeBottomSheetCategories

class CategoriesAdaptor(
    private val context: Context,
) : RecyclerView.Adapter<CategoriesAdaptor.ViewHolder>() {
    private val serviceList: MutableList<GeneralCategory> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(trackerInstance: MutableList<GeneralCategory>) {
        serviceList.clear()
        serviceList.addAll(trackerInstance)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(cart: GeneralCategory) {
            itemView.findViewById<TextView>(R.id.titleCategory).text = cart.titleCategory
            Glide.with(context)
                .load(cart.imageCategory)
                .centerCrop()
                .into(itemView.findViewById(R.id.imageCategory))

            itemView.findViewById<MaterialCardView>(R.id.containerHolders).setOnClickListener {
                InitializeBottomSheetCategories().initializeBottomSheetCategories(
                    context,
                    cart
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.categories_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(serviceList[position])
    }
}