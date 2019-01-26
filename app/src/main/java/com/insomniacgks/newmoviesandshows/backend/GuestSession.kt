package com.insomniacgks.newmoviesandshows.backend

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.preference.PreferenceManager
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

@SuppressLint("StaticFieldLeak")
class GuestSession(var context: Context) : AsyncTask<Void, Void, Boolean>() {

    var asyncTaskResponse: AsyncTaskResponse? = null

    interface AsyncTaskResponse {
        fun processFinish()
    }

    override fun doInBackground(vararg params: Void?): Boolean {
        val link = URL("https://api.themoviedb.org/3/authentication/guest_session/new?api_key=${Constants.API_KEY}")
        val client = link.openConnection() as HttpsURLConnection
        client.requestMethod = "GET"
        client.connect()
        val responseCode = client.responseCode
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val spe: SharedPreferences.Editor = sharedPreferences.edit()
        if (responseCode != 200) {
            spe.putBoolean("success", false)
        }
        val reader = BufferedReader(InputStreamReader(client.inputStream))
        val stringBuilder = StringBuilder()
        while (true) {
            stringBuilder.append(reader.readLine() ?: break)
        }
        val jsonString = stringBuilder.toString()
        val jsonObject = JSONObject(jsonString)
        if (!jsonObject.getBoolean("success")) {
            spe.putBoolean("success", false)
            spe.apply()
            return false
        }
        spe.putBoolean("success", true)
        spe.putString("guest_session_id", jsonObject.getString("guest_session_id"))
        spe.putString("expires_at", jsonObject.getString("expires_at").substring(0, 19))
        spe.apply()
        return true
    }

    override fun onPostExecute(result: Boolean) {
        super.onPostExecute(result)
        asyncTaskResponse!!.processFinish()
    }
}