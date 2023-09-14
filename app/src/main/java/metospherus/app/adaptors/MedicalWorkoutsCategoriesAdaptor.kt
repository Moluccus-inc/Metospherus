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
import metospherus.app.R
import metospherus.app.modules.GeneralWorkoutsCategory

class MedicalWorkoutsCategoriesAdaptor(
    private val context: Context,
) : RecyclerView.Adapter<MedicalWorkoutsCategoriesAdaptor.ViewHolder>() {
    private val serviceList: MutableList<GeneralWorkoutsCategory> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(trackerInstance: MutableList<GeneralWorkoutsCategory>) {
        serviceList.clear()
        serviceList.addAll(trackerInstance)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(workouts: GeneralWorkoutsCategory) {
            itemView.findViewById<TextView>(R.id.workoutCatName).text = workouts.name
            Glide.with(context)
                .load(workouts.image)
                .centerCrop()
                .into(itemView.findViewById(R.id.workingCatImage))

            itemView.findViewById<ImageView>(R.id.workingCatImage).setOnClickListener {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.workout_categories_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(serviceList[position])
    }
}