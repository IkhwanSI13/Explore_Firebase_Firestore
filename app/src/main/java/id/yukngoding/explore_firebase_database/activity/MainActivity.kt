package id.yukngoding.explore_firebase_database.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.yukngoding.explore_firebase_database.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var TAG: String = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnFood.setOnClickListener {
            startActivity(Intent(this, FoodActivity::class.java))
        }
        binding.btnOrigin.setOnClickListener {
            startActivity(Intent(this, OriginActivity::class.java))
        }
    }

}