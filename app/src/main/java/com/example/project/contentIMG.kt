package com.example.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.project.R

private var meesage:String = ""

class contentIMG : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_img)

        meesage = intent.getStringExtra("url").toString()

        val imgAll: ImageView = findViewById(R.id.imgAll)
        Glide.with(applicationContext).load(meesage).into(imgAll);
    }
}