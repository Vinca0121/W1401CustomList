package kr.ac.kumoh.s20180287.prof.w1401customlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import android.graphics.Bitmap
import androidx.collection.LruCache
import kr.ac.kumoh.s20180287.prof.w1401customlist.databinding.ActivityBirdBinding

class BirdActivity : AppCompatActivity() {
    companion object {
        const val KEY_NAME = "BirdName"
        const val KEY_PHOTOGRAPHER = "Photographer"
        const val KEY_IMAGE = "BirdImage"
    }
    private lateinit var binding: ActivityBirdBinding
    private lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBirdBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.imageSong.setImageUrl(intent.getStringExtra(KEY_IMAGE), imageLoader)
        binding.textName.text = intent.getStringExtra(KEY_NAME)
        binding.textPhotographer.text = intent.getStringExtra(KEY_PHOTOGRAPHER)
    }
}