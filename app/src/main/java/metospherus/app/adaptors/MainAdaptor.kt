package metospherus.app.adaptors

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import metospherus.app.R
import metospherus.app.modules.GeneralTemplate
import metospherus.app.utilities.bottomSheetGeneral

class MainAdaptor(private val context: Context, private val lifecycleScope: LifecycleCoroutineScope)
    : RecyclerView.Adapter<MainAdaptor.ViewHolder>() {
    private val serviceList: MutableList<GeneralTemplate> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(trackerInstance: MutableList<GeneralTemplate>) {
        serviceList.clear()
        serviceList.addAll(trackerInstance)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("NewApi")
        fun bind(generalTemplate: GeneralTemplate, context: Context) {
            itemView.findViewById<TextView>(R.id.templateTitle).text = generalTemplate.name
            itemView.findViewById<MaterialCardView>(R.id.containerGen).setOnClickListener {
                bottomSheetGeneral(context, generalTemplate, lifecycleScope)
            }
            Glide.with(context)
                .load(generalTemplate.img)
                .centerCrop()
                .into(itemView.findViewById(R.id.templateImageView))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.template_general_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(serviceList[position], context)
    }
}