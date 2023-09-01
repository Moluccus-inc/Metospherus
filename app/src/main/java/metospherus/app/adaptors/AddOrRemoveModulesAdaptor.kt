package metospherus.app.adaptors

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import metospherus.app.R
import metospherus.app.modules.GeneralTemplate
import metospherus.app.utilities.MoluccusToast

class AddOrRemoveModulesAdaptor(
    private val context: Context,
    private val db: FirebaseDatabase,
    private val auth: FirebaseAuth
) : RecyclerView.Adapter<AddOrRemoveModulesAdaptor.ViewHolder>() {
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
            val containerCardView = itemView.findViewById<MaterialCardView>(R.id.containerGen)
            val templateTitle = itemView.findViewById<TextView>(R.id.templateTitle)
            templateTitle.text = generalTemplate.name

            containerCardView.strokeColor = ContextCompat.getColor(
                context,
                com.google.android.material.R.color.accent_material_dark
            )

            val patientMedicineModulesDB = db.getReference("medicalmodules")
                .child("userspecific")
                .child("modules")
                .child(auth.currentUser!!.uid)

            when (generalTemplate.selected) {
                true -> {
                    containerCardView.strokeWidth = 10
                    containerCardView.isChecked = true
                }
                else -> {
                    containerCardView.strokeWidth = 0
                    containerCardView.isCheckable = true
                }
            }

            containerCardView.setOnClickListener {
                when {
                    generalTemplate.name != "General Health" -> {
                        when (generalTemplate.selected) {
                            true -> {
                                generalTemplate.pushKey?.let { it1 ->
                                    patientMedicineModulesDB.child(it1).child("selected")
                                        .setValue(false)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                // Update your UI here if needed
                                                notifyItemChanged(adapterPosition)
                                            } else {
                                                // Handle the error if the update fails
                                            }
                                        }
                                }
                            }
                            else -> {
                                generalTemplate.pushKey?.let { it1 ->
                                    patientMedicineModulesDB.child(it1).child("selected")
                                        .setValue(true)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                // Update your UI here if needed
                                                notifyItemChanged(adapterPosition)
                                            } else {
                                                // Handle the error if the update fails
                                            }
                                        }
                                }
                            }
                        }
                    }
                    else -> {
                        MoluccusToast(context).showInformation("General Health Can't be Changed")
                    }
                }
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