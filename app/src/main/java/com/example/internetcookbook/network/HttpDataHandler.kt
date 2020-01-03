package com.example.internetcookbook.network

import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL
import java.nio.charset.StandardCharsets

class HttpDataHandler {

    private var stream: String? = null

    fun HTTPDataHandler() {}

    fun getHTTPData(urlString: String?): String? {
        try {
            val url = URL(urlString)
            val urlConnection =
                url.openConnection() as HttpURLConnection
            if (urlConnection.responseCode == 200) {
                val `in`: InputStream =
                    BufferedInputStream(urlConnection.inputStream)
                val r =
                    BufferedReader(InputStreamReader(`in`))
                val sb = StringBuilder()
                var line: String?
                while (r.readLine().also { line = it } != null) sb.append(line)
                stream = sb.toString()
                urlConnection.disconnect()
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stream
    }

    fun PostHttpData(urlString: String?, users: JSONObject) {
        try {
            val url = URL(urlString)
            val urlConnection =
                url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"
            urlConnection.doOutput = true
            val json = users.toString()
            val byteName = json.toByteArray(StandardCharsets.UTF_8)
            val length = byteName.size
            //Error here
            urlConnection.setRequestProperty("Content-Type", "application/json; charset-UTF-8")
            urlConnection.setFixedLengthStreamingMode(length)
            urlConnection.connect()
            urlConnection.outputStream.use { os -> os.write(byteName) }
            urlConnection.disconnect()
        } catch (e: ProtocolException) {
            e.printStackTrace()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun PutHttpData(urlString: String?, newValue: String) {
        try {
            val url = URL(urlString)
            val urlConnection =
                url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "PUT"
            urlConnection.doOutput = true
            val out =
                newValue.toByteArray(StandardCharsets.UTF_8)
            val length = out.size
            urlConnection.setFixedLengthStreamingMode(length)
            urlConnection.setRequestProperty("Content-Type", "application/json; charset-UTF-8")
            urlConnection.connect()
            urlConnection.outputStream.use { os -> os.write(out) }
            val response = urlConnection.inputStream
        } catch (e: ProtocolException) {
            e.printStackTrace()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun DeleteHttpData(urlString: String?, users: JSONObject) {
        try {
            val url = URL(urlString)
            val urlConnection =
                url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "DELETE"
            urlConnection.doOutput = true
            val json = users.toString()
            val byteName =
                json.toByteArray(StandardCharsets.UTF_8)
            val length = byteName.size
            //Error here
            urlConnection.setRequestProperty("Content-Type", "application/json; charset-UTF-8")
            urlConnection.setFixedLengthStreamingMode(length)
            urlConnection.connect()
            urlConnection.outputStream.use { os -> os.write(byteName) }
            val response = urlConnection.inputStream
        } catch (e: ProtocolException) {
            e.printStackTrace()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}