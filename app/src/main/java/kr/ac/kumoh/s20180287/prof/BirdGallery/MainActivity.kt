package kr.ac.kumoh.s20180287.prof.BirdGallery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import kr.ac.kumoh.s20180287.prof.BirdGallery.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var model: BirdViewModel
    // inner class로 정의된 어뎁터를 생성
    private val birdAdapter = BirdAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // lateinit된 모델에 viewModelProvider를 통헤서 BirdViewModel을 할당
        // BirdViewModel은 따로 선언하지 않아도, 프로젝트 내 정의되어져 있으므로 사용가능.
        model = ViewModelProvider(this)[BirdViewModel::class.java]

        // MainActivity의 xml에는 list가 정의되어있고, list는 RecyclerView로 되어있다.
        // apply를 통해서 적용한다.
        binding.list.apply {
            layoutManager = LinearLayoutManager(applicationContext) // layoutManager 적용
            setHasFixedSize(true)                 // RecyclerView의 사이즈가 재 측정되는 것을 막음.
            itemAnimator = DefaultItemAnimator() // RecyclerView의 애니메이션 설정
            adapter = birdAdapter                // RecylerView를 동작하도록하는 adapter 설정
        }
        // BirdViewModel의 list는 LiveData<ArrayList<Bird>>이므로 observe 패턴을 적용.
        // list가 변경되었을 때, recyclerView에 전체 데이터를 추가한다.
        model.list.observe(this) {
            // 연속된 여러개의 아이템을 삽입할 때 사용
            Log.e("test","옵저버 패턴 수행")
            birdAdapter.notifyItemRangeInserted(0, //삽입된 첫번째 아이템의 위치
                model.list.value?.size ?: 0) // 삽입된 아이템의 개수
        }
        //ViewModel에게 data를 요청
        model.requestBirds()
    }

    // 어뎁터는 리사이클러뷰의 기능을 사용하도록 확장하는 추상화다!
    inner class BirdAdapter: RecyclerView.Adapter<BirdAdapter.ViewHolder>() {
        // 뷰 홀더란? 리스트의 틀을 미리 만들어 놓는 것 RecyclerView.viewHodeler, OnClicklistener를 상속받는다.
        // itemView는 ViewHolder 생성 시, 넘어오는 binding된 View 객체이다.
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnClickListener
        {
            //binding된 view에서 TextView와 NetworkImageView를 찾아 할당한다.
            // onBindViewHolder에서 이미지 및 텍스트에 데이터를 출력하는 것이 동작하도록 해야하므로
            // 객체를 생성해서 접근할 수 있도록 한다.
            val txBirdName: TextView = itemView.findViewById(R.id.text1)
            val txPhotographer: TextView = itemView.findViewById(R.id.text2)
            val niImage: NetworkImageView = itemView.findViewById<NetworkImageView>(R.id.image)
            // NetworkImage에 이미지가 표시될 옵션 및 현재 ViewHolder에 OnClickListener를 추가한다.
            init {
                niImage.setDefaultImageResId(android.R.drawable.ic_menu_report_image)
                itemView.setOnClickListener(this)
            }
            // 생성해둔 OnClickListener를 정의한다. (뷰 홀더를 누를 시 동작하도록)
            override fun onClick(p0: View?) {
                // 새로운 화면을 출력 할 Activity를 정의한다.
                val intent = Intent(application, BirdActivity::class.java)
                // 해당 intent에 액티비티에 정의된 KEY값과 adapterPosion을 통해 현재 터치된 뷰홀더의
                // "이름", "포토그래퍼", getImageUrl를 통해 받아온 이미지의 "URL"을 전달한다.
                intent.putExtra(BirdActivity.KEY_NAME,
                    model.list.value?.get(adapterPosition)?.name)
                intent.putExtra(BirdActivity.KEY_PHOTOGRAPHER,
                    model.list.value?.get(adapterPosition)?.photographer)
                intent.putExtra(BirdActivity.KEY_IMAGE,
                    model.getImageUrl(adapterPosition))
                startActivity(intent)
            }
        }

        // 뷰 홀더가 생성 될 때 수행되는 부분
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            // view를 하나 만들어서 item_bird.xml을 바인딩 하고 있다.
            val view = layoutInflater.inflate(
                R.layout.item_bird,
                parent,
                false)
            // 이를 뷰홀더의 view로 사용하도록 한다.
            return ViewHolder(view)
        }
        // 뷰 홀더가 움직일 때, 새로운 데이터가 출력되도록 하는 부분
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.txBirdName.text = model.list.value?.get(position)?.name
            holder.txPhotographer.text = model.list.value?.get(position)?.photographer
            // imgURL과 imageLoader객체를 전달.
            holder.niImage.setImageUrl(model.getImageUrl(position), model.imageLoader)
        }
        // viewHolder 사용을 위해 필수적인 override func이다.
        override fun getItemCount() = model.list.value?.size ?: 0
    }
}