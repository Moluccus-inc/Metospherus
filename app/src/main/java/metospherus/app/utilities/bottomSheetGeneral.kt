package metospherus.app.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.setPeekHeight
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import metospherus.app.R
import metospherus.app.adaptors.MedicineIntakeAdaptor
import metospherus.app.database.localhost.AppDatabase
import metospherus.app.modules.GeneralMenstrualCycle
import metospherus.app.modules.GeneralTemplate
import metospherus.app.trackers.MedicalIntake
import metospherus.app.trackers.PeriodTracker
import metospherus.app.utilities.Constructor.hide
import metospherus.app.utilities.Constructor.insertOrUpdateMenstrualCycles
import metospherus.app.utilities.Constructor.show
import java.util.Locale

@SuppressLint("SetTextI18n")
@RequiresApi(Build.VERSION_CODES.O)
fun bottomSheetGeneral(
    context: Context,
    generalTemplate: GeneralTemplate,
    lifecycleScope: LifecycleCoroutineScope
) {
    MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
        customView(R.layout.general_bottom_sheet)
        cancelOnTouchOutside(false)
        cornerRadius(20f)

        lateinit var recyclerViewTracker: RecyclerView
        val appDatabase = AppDatabase.getInstance(context)

        val closeBottomSheet = view.findViewById<ImageView>(R.id.closeBottomSheet)
        val titleBottomSheet = view.findViewById<TextView>(R.id.titleBottomSheet)

        // Layouts
        val containerMedicalIntake = view.findViewById<RelativeLayout>(R.id.container_medical_intake)
        val containerPeriodTracker = view.findViewById<LinearLayout>(R.id.container_period_tracker)
        val containerFitnessTracker = view.findViewById<NestedScrollView>(R.id.container_fitness_tracker)

        // fab
        val addMedicalInTake = view.findViewById<FloatingActionButton>(R.id.addMedicalInTake)

        titleBottomSheet.text = generalTemplate.name
        closeBottomSheet.setOnClickListener {
            dismiss()
        }

        val auth: FirebaseAuth = Firebase.auth
        val db: FirebaseDatabase = FirebaseDatabase.getInstance()

        val medicineIntakeAdapter = MedicineIntakeAdaptor(context, auth, db)
        when (generalTemplate.name?.trim()?.lowercase(Locale.ROOT)) {
            "Medical Intake".trim().lowercase(Locale.ROOT) -> {
                containerMedicalIntake.show()
                containerPeriodTracker.hide()
                containerFitnessTracker.hide()
                recyclerViewTracker = view.findViewById(R.id.recyclerViewBottomSheet)
                setPeekHeight(Int.MAX_VALUE)
                MedicalIntake().medicalIntakeModule(
                    view,
                    addMedicalInTake,
                    auth,
                    db,
                    context,
                    recyclerViewTracker,
                    medicineIntakeAdapter
                )
            }

            "Period Tracker".trim().lowercase(Locale.ROOT) -> {
                containerPeriodTracker.show()
                containerMedicalIntake.hide()
                containerFitnessTracker.hide()
                PeriodTracker().periodTrackerModule(
                    view,
                    lifecycleScope,
                    appDatabase,
                    context,
                    auth,
                    db
                )
            }

            "Fitness Health".trim().lowercase(Locale.ROOT) -> {
                containerMedicalIntake.hide()
                containerPeriodTracker.hide()
                containerFitnessTracker.show()
            }

            else -> {
                containerMedicalIntake.hide()
                containerPeriodTracker.hide()
                containerFitnessTracker.hide()
            }
        }

        onShow {
            auth.currentUser?.let { currentUser ->
                val menstrualCyclesManager = db.getReference("medicalmodules").child("userspecific").child("menstrual").child(currentUser.uid)
                menstrualCyclesManager.keepSynced(true)
                menstrualCyclesManager.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val menstrualCycles = snapshot.getValue(
                                GeneralMenstrualCycle::class.java
                            )
                            if (menstrualCycles != null) {
                                lifecycleScope.launch {
                                    insertOrUpdateMenstrualCycles(
                                        menstrualCycles,
                                        appDatabase
                                    )
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle the error if needed.
                    }
                })
            }
        }
    }
}

fun updateFirebaseDatabase(
    id: String,
    value: String,
    auth: FirebaseAuth,
    db: FirebaseDatabase
) {
    if (value != "null" && value != "Not Set") {
        val databaseRef = db.getReference("medicalmodules")
            .child("userspecific").child("menstrual")
            .child(auth.currentUser!!.uid)
        databaseRef.child(id).setValue(value)
    }
}