package kr.ac.kumoh.s20180287.prof.w1401customlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.volley.toolbox.ImageLoader
import kr.ac.kumoh.s20180287.prof.w1401customlist.databinding.ActivitySongBinding
import com.android.volley.toolbox.Volley
import android.graphics.Bitmap
import androidx.collection.LruCache

class SongActivity : AppCompatActivity() {
    companion object {
        const val KEY_TITLE = "SongTitle"
        const val KEY_SINGER = "SongSinger"
        const val KEY_IMAGE = "SongImage"
    }
    private lateinit var binding: ActivitySongBinding
    private lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
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
        binding.textTitle.text = intent.getStringExtra(KEY_TITLE)
        binding.textSinger.text = intent.getStringExtra(KEY_SINGER)
    }
}