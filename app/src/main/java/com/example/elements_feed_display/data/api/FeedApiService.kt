package com.example.elements_feed_display.data.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.elements_feed_display.data.model.ListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class FeedApiService (){
    private val baseUrl = "https://dummyjson.com/"
    private val SHOW_IMAGES = true

    suspend fun getFeedById(id: Int): Result<ListItem>{
        val urlString = "${baseUrl}products?limit=1&skip=${id}"
        return try {
            val conn = (URL(urlString).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 5000
                readTimeout  = 5000
                doInput = true
            }

            if (conn.responseCode == HttpURLConnection.HTTP_OK){
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val response = reader.use { it.readText() }

                val feed = if (SHOW_IMAGES) {
                    parseFeedImage(response)
                } else parseFeed(response)

                if (feed != null) {
                    Result.success(feed)
                } else {
                    Result.failure(Exception("Parsing error: No feed found"))
                }
            } else {
                Result.failure(Exception("HTTP error: ${conn.responseCode}"))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    private fun parseFeed(json: String): ListItem.Feed? {
        val root = JSONObject(json)
        val products = root.optJSONArray("products") ?: return null
        if (products.length() == 0) return null

        val product = products.getJSONObject(0)
        return ListItem.Feed(
            content = product.optString("description", ""),
            time = System.currentTimeMillis()
        )
    }

    private suspend fun parseFeedImage(json: String): ListItem.FeedImage? {
        val root = JSONObject(json)
        val products = root.optJSONArray("products") ?: return null
        if (products.length() == 0) return null

        val product = products.getJSONObject(0)

        val imageUrl = product.optString("thumbnail")
        var bitmap: Bitmap? = null

        if (!imageUrl.isNullOrEmpty()) {
            try {
                withContext(Dispatchers.IO) {
                    val inputStream = URL(imageUrl).openStream()
                    bitmap = BitmapFactory.decodeStream(inputStream)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return ListItem.FeedImage(
            image = bitmap,
            content = product.optString("description", ""),
            time = System.currentTimeMillis()
        )
    }
}