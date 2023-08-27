package metospherus.app.adaptors

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import metospherus.app.R
import metospherus.app.modules.GeneralPills
import metospherus.app.services.AlertManager

class MedicineIntakeAdaptor(
    private val context: Context,
    private val auth: FirebaseAuth,
    private val db: FirebaseDatabase
) :
    RecyclerView.Adapter<MedicineIntakeAdaptor.ViewHolder>() {
    private val serviceList: MutableList<GeneralPills> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(trackerInstance: MutableList<GeneralPills>) {
        serviceList.clear()
        serviceList.addAll(trackerInstance)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.medicine_intake_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(serviceList[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
        fun bind(generalPill: GeneralPills) {
            itemView.findViewById<TextView>(R.id.titleMedicine).text = generalPill.medicineName
            itemView.findViewById<TextView>(R.id.medicineTime).text = generalPill.medicineTime
            itemView.findViewById<Chip>(R.id.medicinePeriod).text = generalPill.medicineDate

            AlertManager().createAlert(
                context, generalPill.medicineName.toString(),
                "reminder to take you medicine in time", generalPill.medicineTime.toString()
            )
            when {
                generalPill.medicineAmount!!.contains("1") -> {
                    itemView.findViewById<Chip>(R.id.medicinePrescrition).text =
                        "${generalPill.medicineAmount} pill"
                }

                else -> {
                    itemView.findViewById<Chip>(R.id.medicinePrescrition).text =
                        "${generalPill.medicineAmount} pills"
                }
            }

            itemView.findViewById<FloatingActionButton>(R.id.deleteMedicine).setOnClickListener {
                val patientMedicineModulesDB = db.getReference("medicalmodules")
                    .child("userspecific")
                    .child("medicineIntake")
                    .child(auth.currentUser!!.uid)
                    .child(generalPill.pushkey.toString())
                patientMedicineModulesDB.removeValue()
            }

            val drawableResId = when (generalPill.medicineAvatar) {
                "1" -> R.drawable.picture_pill
                "2" -> R.drawable.injection
                "3" -> R.drawable.patch
                else -> R.drawable.picture_pill
            }

            val drawable = context.getDrawable(drawableResId)
            when {
                drawable != null -> {
                    Glide.with(context)
                        .load(drawable)
                        .centerCrop()
                        .into(itemView.findViewById(R.id.medicineAvatar))
                }
            }

        }
    }
}