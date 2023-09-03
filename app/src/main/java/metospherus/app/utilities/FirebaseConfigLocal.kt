package metospherus.app.utilities

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object FirebaseConfigLocal {
    fun createDatabase(path: String, value: Any, db: FirebaseDatabase): Task<Void> {
        return db.getReference(path).setValue(value)
    }

    fun createDatabaseWithKey(path: String, value: Any, db: FirebaseDatabase): Task<Void> {
        return db.getReference(path).push().setValue(value)
    }

    fun retrieveDatabase(path: String, db: FirebaseDatabase): ValueEventListener {
        return db.getReference(path).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                //
            }
        })
    }

    fun retrieveDatabaseMultipleKeys(
        path: String,
        keyNumbers: Int,
        db: FirebaseDatabase
    ): ValueEventListener {
        return db.getReference(path).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                when (keyNumbers) {
                    1 -> {
                        for (snapshotItem in snapshot.children) {
                            if (snapshotItem.exists()) {
                            }
                            // return the snapshot
                        }
                    }

                    2 -> {
                        for (snapshotItem in snapshot.children) {
                            for (snapshotItem2 in snapshotItem.children) {
                                if (snapshotItem2.exists()) {
                                }
                                // return the snapshot
                            }
                        }
                    }

                    3 -> {

                    }

                    4 -> {

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //
            }
        })
    }

    fun updateChildren(
        mapValues: Map<String, String>,
        db: DatabaseReference
    ): Task<Void> {
        return db.updateChildren(mapValues)
    }
}