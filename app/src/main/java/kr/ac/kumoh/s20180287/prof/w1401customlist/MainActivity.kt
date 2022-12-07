package kr.ac.kumoh.s20180287.prof.w1401customlist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import kr.ac.kumoh.s20180287.prof.w1401customlist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var model: BirdViewModel
    private val songAdapter = SongAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        model = ViewModelProvider(this)[BirdViewModel::class.java]

        binding.list.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = songAdapter
        }

        model.list.observe(this) {
            // 좀더 구체적인 이벤트를 사용하라고 warning 나와서 변경함
            //songAdapter.notifyDataSetChanged()
            //Log.i("size", "${model.list.value?.size ?: 0}")

            // Changed가 아니라 Inserted
            songAdapter.notifyItemRangeInserted(0,
                model.list.value?.size ?: 0)
        }

        model.requestBirds()
    }

    inner class SongAdapter: RecyclerView.Adapter<SongAdapter.ViewHolder>() {
        //inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        inner class ViewHolder(itemView: View)
            : RecyclerView.ViewHolder(itemView), OnClickListener {

            val txBirdName: TextView = itemView.findViewById(R.id.text1)
            val txPhotographer: TextView = itemView.findViewById(R.id.text2)

            val niImage: NetworkImageView = itemView.findViewById<NetworkImageView>(R.id.image)

            init {
                niImage.setDefaultImageResId(android.R.drawable.ic_menu_report_image)
                itemView.setOnClickListener(this)
            }

            override fun onClick(p0: View?) {
//                Toast.makeText(application,
//                    model.list.value?.get(adapterPosition)?.title,
//                    Toast.LENGTH_SHORT).show()
                val intent = Intent(application, BirdActivity::class.java)
                intent.putExtra(BirdActivity.KEY_NAME,
                    model.list.value?.get(adapterPosition)?.name)
                intent.putExtra(BirdActivity.KEY_PHOTOGRAPHER,
                    model.list.value?.get(adapterPosition)?.photographer)
                intent.putExtra(BirdActivity.KEY_IMAGE,
                    model.getImageUrl(adapterPosition))
                startActivity(intent)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            //val view = layoutInflater.inflate(android.R.layout.simple_list_item_2,
            val view = layoutInflater.inflate(R.layout.item_song,
                parent,
                false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.txBirdName.text = model.list.value?.get(position)?.name
            holder.txPhotographer.text = model.list.value?.get(position)?.photographer

            holder.niImage.setImageUrl(model.getImageUrl(position), model.imageLoader)
        }

        override fun getItemCount() = model.list.value?.size ?: 0
    }
}