package metospherus.app.update

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import metospherus.app.BuildConfig
import metospherus.app.MainActivity
import metospherus.app.R
import metospherus.app.utilities.MoluccusToast
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class UpdateUtil(var context: Context) {
    private val tag = "UpdateUtil"
    private val downloadManager: DownloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    fun updateApp(result: (result: String) -> Unit) {
        try {
            if (updatingApp) {
                Toast.makeText(
                    context,
                    context.getString(R.string.already_updating),
                    Toast.LENGTH_LONG
                ).show()
            }
            val res = checkForAppUpdate()
            val version: String
            val body: String?
            try {
                version = res.getString("tag_name")
                body = res.getString("body")
            } catch (e: JSONException) {
                result(context.getString(R.string.network_error))
                return
            }
            val versionNameInt = version.split("v")[1].replace(".", "").toInt()
            val currentVersionNameInt = BuildConfig.VERSION_NAME.replace(".", "").toInt()
            if (currentVersionNameInt >= versionNameInt) {
                result(context.getString(R.string.you_are_in_latest_version))
                return
            }
            Handler(Looper.getMainLooper()).post {
                val updateDialog = MaterialAlertDialogBuilder(context)
                    .setTitle(version)
                    .setMessage(body)
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setNegativeButton("Cancel") { dims: DialogInterface?, _: Int ->
                        dims!!.dismiss()
                    }
                    .setPositiveButton("Update") { dims: DialogInterface?, _: Int ->
                        dims!!.dismiss()
                        startAppUpdate(
                            res
                        )
                    }
                updateDialog.show()
            }
            return
        } catch (e: Exception) {
            e.printStackTrace()
            result(e.message.toString())
            return
        }
    }

    private fun checkForAppUpdate(): JSONObject {
        val url = "https://api.github.com/repos/Moluccus-inc/Metospherus/releases/latest"
        val reader: BufferedReader
        var line: String?
        val responseContent = StringBuilder()
        val conn: HttpURLConnection
        var json = JSONObject()
        try {
            val req = URL(url)
            conn = req.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 10000
            conn.readTimeout = 5000
            if (conn.responseCode < 300) {
                reader = BufferedReader(InputStreamReader(conn.inputStream))
                while (reader.readLine().also { line = it } != null) {
                    responseContent.append(line)
                }
                reader.close()
                json = JSONObject(responseContent.toString())
                if (json.has("error")) {
                    throw Exception()
                }
            }
            conn.disconnect()
        } catch (e: Exception) {
            Log.e(tag, e.toString())
        }
        return json
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun startAppUpdate(updateInfo: JSONObject) {
        try {
            val versions = updateInfo.getJSONArray("assets")
            var url = ""
            var appName = ""
            for (i in 0 until versions.length()) {
                val tmp = versions.getJSONObject(i)
                if (tmp.getString("name").contains(Build.SUPPORTED_ABIS[0])) {
                    url = tmp.getString("browser_download_url")
                    appName = tmp.getString("name")
                    break
                }
            }
            if (url.isEmpty()) {
                Toast.makeText(context, R.string.couldnt_find_apk, Toast.LENGTH_SHORT).show()
                return
            }
            val uri = Uri.parse(url)
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            downloadsDir.mkdirs()
            val downloadRequest = DownloadManager.Request(uri)
                .setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
                )
                .setAllowedOverRoaming(true)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(context.getString(R.string.downloading_update))
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, appName)

            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadId = downloadManager.enqueue(downloadRequest)

            val onCompleteReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                        val downloadCompletedId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                        if (downloadCompletedId == downloadId) {
                            val installIntent = Intent(Intent.ACTION_VIEW)
                            val apkUri = FileProvider.getUriForFile(context!!, context.applicationContext.packageName + ".provider", File(downloadsDir, appName))
                            installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive")
                            installIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(installIntent)
                            context.unregisterReceiver(this)

                            val restartIntent = Intent(context, MainActivity::class.java)
                            restartIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(restartIntent)
                        }
                    }
                }
            }

            // Register the BroadcastReceiver to listen for download completion
            val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            context.registerReceiver(onCompleteReceiver, filter)
        } catch (ignored: Exception) {
            // Handle exceptions
            MoluccusToast(context).showError("Error exception ${ignored.message}")
        }
    }

    companion object {
        var updatingApp = false
    }
}