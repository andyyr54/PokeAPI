package com.example.pokeapi

import android.provider.ContactsContract.CommonDataKinds.Website.URL
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class makeAPIRequest {
    fun getRequest(sUrl: String): String? {
        val inputStream: InputStream
        var result: String? = null

        try {
            // Create URL
            val url = URL(sUrl)

            // Create HttpURLConnection
            val conn: HttpsURLConnection = url.openConnection() as HttpsURLConnection

            // Launch GET request
            conn.connect()

            // Receive response as inputStream
            inputStream = conn.inputStream

            if (inputStream != null)
            // Convert input stream to string
                result = inputStream.bufferedReader().use(BufferedReader::readText)
            else
                result = "error: inputStream is null"

        } catch (err: Error) {
            print("Error when executing get request:" + err.localizedMessage)

        }
        return result
    }
}
