package kr.ac.kumoh.s20180287.prof.BirdGallery

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

    // static과 같은 역할을 한다. 객체의 생성이 없어도 사용 가능.
    companion object {
        const val QUEUE_TAG = "BirdVolleyRequest"
        const val SERVER_URL = "https://appprograming-hw-phsgv.run.goorm.io"
    }                           // https://appprograming-hw-phsgv.run.goorm.io/bird

    private val birds_Data = ArrayList<Bird>()
    // Livedata는 추상클래스 이므로 직접생성이 불가능하다.
    // 변경이 불가능한 Mutable쪽만 공개해야 하므로 해당 방식으로 사용한다.
    private val _list = MutableLiveData<ArrayList<Bird>>()
    val list: LiveData<ArrayList<Bird>>
        get() = _list

    private var queue: RequestQueue   // 서버로 요청하기 위한 queue
    val imageLoader: ImageLoader      // 서버에서 이미지를 가져오기 위한 imageLoder

    init {
        //_list.value = birds_Data
        // queue에 Volly를 통한 새로운 RequestQueue를 만들어 할당한다.
        queue = Volley.newRequestQueue(getApplication())
        // 이미지로더 객체를 만들어 할당한다.
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

    // Birds데이터를 서버에서 요청
    fun requestBirds() {
        // JsonArray를 요청하는 JsonArrayRequest 생성
        val request = JsonArrayRequest(
            Request.Method.GET,
            "$SERVER_URL/bird",
            null,
            {
                birds_Data.clear()  // bird_Data는 단순히 ArrayList<Bird>
                parseJson(it)       // Json을 파싱하는 함수 실행 ("it"은 전체 데이터)
                _list.value = birds_Data  // 저장된 전체 데이터들을 MutableLivedata에 넣는다.
            },
            {
                Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
            }
        )
        // 태그를 지정 후, Request 큐에 추가하여 실행
        request.tag = QUEUE_TAG
        queue.add(request)
    }
    // 전체 데이터 it을 items으로 받아옴
    private fun parseJson(items: JSONArray) {
        // item.lenght만큼 반복
        for (i in 0 until items.length()) {
            // 데이터에 인덱스순서대로 접근하여 item을 가져온다.
            val item: JSONObject = items[i] as JSONObject
            // 각 item에 딕셔너리 형태로 저장되어 있으므로 key값을 통해서 데이터에 접근한다.
            val id = item.getInt("id")
            val name = item.getString("name")
            val photographer = item.getString("photographer")
            val image = item.getString("image")
            // 해당 데이터를 기존 만들어둔, data Class 형태로 만들어 추가한다.
            birds_Data.add(Bird(id, name, photographer, image))
        }
    }

    // viewModel을 초기화 해주는 함수
    override fun onCleared() {
        super.onCleared()
        queue.cancelAll(QUEUE_TAG) // queue에 담긴 명령도 취소한다.
    }
}