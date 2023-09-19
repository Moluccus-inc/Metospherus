package metospherus.app.utilities

import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object FirebaseConfig {
    /**
     * Retrieve Realtime Database
     * @param db database reference
     * @param path database path reference
     * @return database reference
     * @author Tiabah La Niina
     */
    fun retrieveRealtimeDatabase(db: FirebaseDatabase, path: Any): DatabaseReference {
        return db.getReference(path.toString())
    }

    /**
     * Retrieve RealTime Database OnListener
     * @param db database reference
     * @param path database path reference
     * @param onDataChange request for data change listener
     * @author Tiabah La Niina
     */
    fun retrieveRealtimeDatabaseOnListener(
        db: FirebaseDatabase,
        path: String,
        context: Context,
        onDataChange: (DataSnapshot) -> Unit
    ) {
        db.getReference(path).let {
            it.keepSynced(true)
            it.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    onDataChange(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    MoluccusToast(context = context).showError(error.details)
                }
            })
        }
    }

    /**
     * Update Realtime Database
     * @param db firebase database
     * @param path firebase path
     * @param value firebase value
     */
    fun updateRealtimeDatabaseData(
        db: FirebaseDatabase,
        path: String,
        value : String
    ) {
        if (value.isNotEmpty()) {
            db.getReference(path).setValue(value)
        }
    }
}