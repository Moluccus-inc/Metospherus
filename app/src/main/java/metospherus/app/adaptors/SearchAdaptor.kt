package metospherus.app.adaptors

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import metospherus.app.R
import metospherus.app.modules.GeneralSearchResults

class SearchAdaptor(
    private val context: Context,
) : RecyclerView.Adapter<SearchAdaptor.ViewHolder>() {
    private val serviceList: MutableList<GeneralSearchResults> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(searchInstance: MutableList<GeneralSearchResults>) {
        serviceList.clear()
        serviceList.addAll(searchInstance)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(searchResults: GeneralSearchResults) {
            itemView.findViewById<TextView>(R.id.responseInformation).text = searchResults.searchResponse
            itemView.findViewById<TextView>(R.id.metospherusModuleName).text = searchResults.searchAuthor
            Glide.with(context)
                .load(R.mipmap.ic_launcher_round)
                .centerCrop()
                .into(itemView.findViewById(R.id.metospherusModuleIcon))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.search_view_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(serviceList[position])
    }
}