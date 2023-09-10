package metospherus.app.adaptors

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import metospherus.app.R
import metospherus.app.database.profile_data.Profiles

class MedicalProfessionsAdaptor(
    private val context: Context,
    private val auth: FirebaseAuth,
    private val db: FirebaseDatabase
) : RecyclerView.Adapter<MedicalProfessionsAdaptor.ViewHolder>(){
    private val serviceList: MutableList<Profiles> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(trackerInstance: MutableList<Profiles>) {
        serviceList.clear()
        serviceList.addAll(trackerInstance)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("NewApi")
        fun bind(generalTemplate: Profiles, context: Context) {
            itemView.findViewById<TextView>(R.id.medicalNameHolder).text = generalTemplate.generalDescription.usrPreferedName
            itemView.findViewById<TextView>(R.id.medicalAboutHolder).text = generalTemplate.medicalProfessionals.about
            itemView.findViewById<TextView>(R.id.medicalSpacificationHolder).text = generalTemplate.medicalProfessionals.medicalProfessionType
            itemView.findViewById<FloatingActionButton>(R.id.medicalProfessionalsQuotations).setOnClickListener {

            }

            itemView.findViewById<FloatingActionButton>(R.id.medicalProfessionalsInquiry).setOnClickListener {

            }

            Glide.with(context)
                .load(generalTemplate.avatar)
                .placeholder(R.drawable.holder)
                .centerCrop()
                .into(itemView.findViewById<ShapeableImageView>(R.id.medicalAvatarHolder))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.medical_professionals_profile, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(serviceList[position], context)
    }
}
