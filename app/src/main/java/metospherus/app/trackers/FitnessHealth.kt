package metospherus.app.trackers

import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.internal.main.DialogLayout
import com.facebook.shimmer.Shimmer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton
import metospherus.app.R
import metospherus.app.adaptors.MedicalWorkoutsCategoriesAdaptor
import metospherus.app.modules.GeneralTemplate
import metospherus.app.modules.GeneralWorkoutsCategory
import metospherus.app.utilities.Constructor.show
import metospherus.app.utilities.FirebaseConfig.retrieveRealtimeDatabaseOnListener
import metospherus.app.utilities.MoluccusToast

class FitnessHealth {
    fun fitnessHealthModule(
        view: DialogLayout,
        auth: FirebaseAuth,
        db: FirebaseDatabase,
        materialDialog: MaterialDialog
    ) {
        val containerFitnessTracker =
            view.findViewById<NestedScrollView>(R.id.container_fitness_tracker)
        containerFitnessTracker.show()

        val profileDetails =
            db.getReference("medicalmodules").child("userspecific").child("workOutFitness")
        profileDetails.keepSynced(true)

        val medicalWorkoutsCategory = MedicalWorkoutsCategoriesAdaptor(view.context)
        val recyclerViewWorkoutCategories =
            view.findViewById<RecyclerView>(R.id.recyclerViewWorkoutCategories)
        // TextView Holders
        val workOutCompleted = view.findViewById<TextView>(R.id.workOutCompleted)
        val workoutInProgress = view.findViewById<TextView>(R.id.workoutInProgress)
        val workouttimeSpent = view.findViewById<TextView>(R.id.workouttimeSpent)
        // Card
        val workoutProgressTracking = view.findViewById<LinearLayout>(R.id.workoutProgresTracking)
        val personalizedWorkoutsLayout =
            view.findViewById<HorizontalScrollView>(R.id.personalizedWorkoutsLayout)
        val discoverNewWorkOutsLayout =
            view.findViewById<HorizontalScrollView>(R.id.discoverNewWorkOutsLayout)

        recyclerViewWorkoutCategories.layoutManager = LinearLayoutManager(
            view.context, LinearLayoutManager.HORIZONTAL,
            false
        )
        recyclerViewWorkoutCategories.adapter = medicalWorkoutsCategory

        recyclerViewWorkoutCategories.loadSkeleton(R.layout.workout_categories_layout) {
            itemCount(6)
        }
        retrieveRealtimeDatabaseOnListener(
            db,
            "medicalmodules/userspecific/workOutFitness/categories",
            view.context,
            onDataChange = { valuedataSnapshot ->
                val categoryTemps = mutableListOf<GeneralWorkoutsCategory>()
                if (valuedataSnapshot.exists()) {
                    categoryTemps.clear()
                    for (categorySnapshot in valuedataSnapshot.children) {
                        val categories =
                            categorySnapshot.getValue(GeneralWorkoutsCategory::class.java)
                        if (categories != null) {
                            categoryTemps.add(categories)
                            recyclerViewWorkoutCategories.hideSkeleton()
                        }
                    }
                    medicalWorkoutsCategory.setData(categoryTemps)
                }
            })

        auth.currentUser?.uid?.let {
            profileDetails.child(it).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    when {
                        snapshot.exists() -> {
                            discoverNewWorkOutsLayout.hideSkeleton()
                            workoutProgressTracking.hideSkeleton()
                            personalizedWorkoutsLayout.hideSkeleton()
                            recyclerViewWorkoutCategories.hideSkeleton()

                            workOutCompleted.text = snapshot.child("workoutProgressTracking")
                                .child("worksOutCompleted").value.toString()
                            workoutInProgress.text = snapshot.child("workoutProgressTracking")
                                .child("workoutInProgress").value.toString()
                            workouttimeSpent.text = snapshot.child("workoutProgressTracking")
                                .child("workouttimeSpent").value.toString()


                        }

                        else -> {
                            val workoutProgressTrackingModules = mapOf(
                                "workoutProgressTracking" to mapOf(
                                    "worksOutCompleted" to 0,
                                    "workoutInProgress" to 0,
                                    "workouttimeSpent" to 0,
                                )
                            )
                            profileDetails.child(it).updateChildren(workoutProgressTrackingModules)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    MoluccusToast(materialDialog.windowContext).showError("Cancelled ${error.message}")
                }
            })
        }

        workoutProgressTracking.loadSkeleton {
            val customShimmer = Shimmer.AlphaHighlightBuilder()
                .setDirection(Shimmer.Direction.TOP_TO_BOTTOM)
                .build()
            shimmer(customShimmer)
        }
        discoverNewWorkOutsLayout.loadSkeleton {
            val customShimmer = Shimmer.AlphaHighlightBuilder()
                .setDirection(Shimmer.Direction.TOP_TO_BOTTOM)
                .build()
            shimmer(customShimmer)
        }
        personalizedWorkoutsLayout.loadSkeleton {
            val customShimmer = Shimmer.AlphaHighlightBuilder()
                .setDirection(Shimmer.Direction.TOP_TO_BOTTOM)
                .build()
            shimmer(customShimmer)
        }
        recyclerViewWorkoutCategories.loadSkeleton(R.layout.workout_categories_layout) {
            itemCount(12)
        }
        materialDialog.setOnDismissListener {
            discoverNewWorkOutsLayout.hideSkeleton()
            workoutProgressTracking.hideSkeleton()
            personalizedWorkoutsLayout.hideSkeleton()
            recyclerViewWorkoutCategories.hideSkeleton()
        }
    }
}