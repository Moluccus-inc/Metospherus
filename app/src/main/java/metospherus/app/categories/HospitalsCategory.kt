package metospherus.app.categories

import androidx.core.widget.NestedScrollView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import metospherus.app.R
import metospherus.app.utilities.Constructor.show

class HospitalsCategory() {
    fun initializeHospitalsCategory(
        materialDialog: MaterialDialog,
        auth: FirebaseAuth,
        db: FirebaseDatabase
    ) {
        materialDialog.view.findViewById<NestedScrollView>(R.id.medicalHospitalCategory).show()


    }
}