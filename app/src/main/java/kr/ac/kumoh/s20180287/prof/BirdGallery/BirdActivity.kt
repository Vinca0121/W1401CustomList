package kr.ac.kumoh.s20180287.prof.BirdGallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import android.graphics.Bitmap
import androidx.collection.LruCache
import kr.ac.kumoh.s20180287.prof.BirdGallery.databinding.ActivityBirdBinding

class BirdActivity : AppCompatActivity() {
    companion object {
        const val KEY_NAME = "BirdName"
        const val KEY_PHOTOGRAPHER = "Photographer"
        const val KEY_IMAGE = "BirdImage"
    }
    private lateinit var binding: ActivityBirdBinding
    // private으로 imageLoader를 정의한다.
    private lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBirdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Activity이므로, newRequestQueue에 this를 사용할 수 있다.
        imageLoader = ImageLoader(Volley.newRequestQueue(this),
            object : ImageLoader.ImageCache {
                private val cache = LruCache<String, Bitmap>(100)
                override fun getBitmap(url: String): Bitmap? {
                    return cache.get(url)
                }
                override fun putBitmap(url: String, bitmap: Bitmap) {
                    cache.put(url, bitmap)
                }
            })

        // 생성될 때, intent를 통해서 넣어져있던 값 들을 KEY를 이용하여 가져오고 출력해준다.
        binding.imageBird.setImageUrl(intent.getStringExtra(KEY_IMAGE), imageLoader)
        binding.textName.text = intent.getStringExtra(KEY_NAME)
        binding.textPhotographer.text = intent.getStringExtra(KEY_PHOTOGRAPHER)
    }
}