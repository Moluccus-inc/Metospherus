package metospherus.app.update

import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import metospherus.app.BuildConfig
import metospherus.app.R
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class UpdateUtil(var context: Context) {
    private val tag = "UpdateUtil"
    private val downloadManager: DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

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
            val versionNameInt = version.split("v")[1].replace(".","").toInt()
            val currentVersionNameInt = BuildConfig.VERSION_NAME.replace(".","").toInt()
            if (currentVersionNameInt >= versionNameInt) {
                result(context.getString(R.string.you_are_in_latest_version))
                return
            }
            Handler(Looper.getMainLooper()).post {
                val updateDialog = MaterialAlertDialogBuilder(context)
                    .setTitle(version)
                    .setMessage(body)
                    .setIcon(R.drawable.file_download)
                    .setNegativeButton("Cancel") { _: DialogInterface?, _: Int -> }
                    .setPositiveButton("Update") { _: DialogInterface?, _: Int ->
                        startAppUpdate(
                            res
                        )
                    }
                updateDialog.show()
            }
            return
        }catch (e: Exception){
            e.printStackTrace()
            result(e.message.toString())
            return
        }
    }

    private fun checkForAppUpdate(): JSONObject {
        val url = "https://api.github.com/repos/deniscerri/ytdlnis/releases/latest"
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
            Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .mkdirs()
            downloadManager.enqueue(
                DownloadManager.Request(uri)
                    .setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_WIFI or
                                DownloadManager.Request.NETWORK_MOBILE
                    )
                    .setAllowedOverRoaming(true)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setTitle(context.getString(R.string.downloading_update))
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, appName)
            )
        } catch (ignored: Exception) {
        }
    }

    companion object {
        var updatingApp = false
    }
}