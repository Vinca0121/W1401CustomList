package kr.ac.kumoh.s20180287.prof.w1401customlist

import android.app.Application
import android.graphics.Bitmap
import android.widget.Toast
import androidx.collection.LruCache
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

class BirdViewModel(application: Application) : AndroidViewModel(application) {
    data class Bird (var id: Int, var name: String, var photographer: String, var image: String)

    companion object {
        const val QUEUE_TAG = "BirdVolleyRequest"

        // NOTE: 서버 주소는 본인의 서버 IP 사용할 것
        const val SERVER_URL = "https://appprograming-hw-phsgv.run.goorm.io"
    }                           // https://appprograming-hw-phsgv.run.goorm.io/bird

    private val birds_Data = ArrayList<Bird>()
    private val _list = MutableLiveData<ArrayList<Bird>>()
    val list: LiveData<ArrayList<Bird>>
        get() = _list

    private var queue: RequestQueue
    val imageLoader: ImageLoader

    init {
        _list.value = birds_Data
        queue = Volley.newRequestQueue(getApplication())
        imageLoader = ImageLoader(queue,
            object : ImageLoader.ImageCache {
                private val cache = LruCache<String, Bitmap>(100)
                override fun getBitmap(url: String): Bitmap? {
                    return cache.get(url)
                }
                override fun putBitmap(url: String, bitmap: Bitmap) {
                    cache.put(url, bitmap)
                }
            })
    }
    fun getImageUrl(i: Int): String = "$SERVER_URL/image/" + URLEncoder.encode(birds_Data[i].image, "utf-8")

    fun requestBirds() {
        val request = JsonArrayRequest(
            Request.Method.GET,
            "$SERVER_URL/bird",
            null,
            {
                //Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
                birds_Data.clear()
                parseJson(it)
                _list.value = birds_Data
            },
            {
                Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
            }
        )

        request.tag = QUEUE_TAG
        queue.add(request)
    }

    private fun parseJson(items: JSONArray) {
        for (i in 0 until items.length()) {
            val item: JSONObject = items[i] as JSONObject
            val id = item.getInt("id")
            val name = item.getString("name")
            val photographer = item.getString("photographer")
            val image = item.getString("image")

            birds_Data.add(Bird(id, name, photographer, image))
        }
    }

    override fun onCleared() {
        super.onCleared()
        queue.cancelAll(QUEUE_TAG)
    }
}