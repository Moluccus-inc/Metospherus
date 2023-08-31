package metospherus.app.widgets

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import metospherus.app.R
class MedicineReminderWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete any associated preferences or data
        for (appWidgetId in appWidgetIds) {
            // Delete preferences or data related to appWidgetId
        }
    }

    // Override other lifecycle methods as needed
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.medicine_reminder_widget_provider)
    val auth = Firebase.auth.currentUser
    val db = FirebaseDatabase.getInstance()

    if (auth != null) {
        val databaseRefWidget = db.getReference("medicalmodules")
            .child("userspecific").child("medicineIntake")
            .child(auth.uid)
        databaseRefWidget.keepSynced(true)
        databaseRefWidget.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val remindersCount = snapshot.childrenCount.toString()
                    views.setTextViewText(R.id.widgetMedicalReminders, remindersCount)
                    // Update other views in the widget as needed
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled as needed
            }
        })
    } else {
        views.setTextViewText(R.id.widgetMedicalReminders, "0")
        // Update other views in the widget as needed
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}