package metospherus.app

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import metospherus.app.modules.GeneralCategory
import metospherus.app.utilities.InitializeBottomSheetCategories

class DeepLinkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data: Uri? = intent.data
        if (data != null) {
            val host = data.host
            if (host == "docs") {
                InitializeBottomSheetCategories()
                    .initializeBottomSheetCategories(this, GeneralCategory(
                        "Documents",
                        "",
                        true
                    ))
            }
        }

        finish() // Close the activity
    }
}
