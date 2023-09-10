package metospherus.app.trackers

import androidx.core.widget.NestedScrollView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import metospherus.app.R
import metospherus.app.utilities.Constructor.hide
import metospherus.app.utilities.Constructor.show

class GeneralHealth {
    fun generalHealthModule(
        viewDialogSheet: MaterialDialog,
        auth: FirebaseAuth,
        db: FirebaseDatabase,
    ) {
        val medicalGeneralHealthModule = viewDialogSheet.findViewById<NestedScrollView>(R.id.medicalGeneralHealthModule)
        medicalGeneralHealthModule.show()

        viewDialogSheet.setOnDismissListener {
            medicalGeneralHealthModule.hide()
        }
    }
}