package com.example.project

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.project.R
import com.example.project.databinding.ActivityContentReproBinding


class contentRepro : AppCompatActivity() {

    private var meesage:String = ""
    private lateinit var binding: ActivityContentReproBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityContentReproBinding.inflate(layoutInflater)
        setContentView(binding.root)

        meesage = intent.getStringExtra("url").toString()


        binding.exoPlayerView.prepare(Uri.parse(meesage))

        val playerView = binding.exoPlayerView

        val fullscreenButton = playerView.findViewById<ImageView>(R.id.exo_fullscreen_icon)

        var fullscreen = false

        fullscreenButton.setOnClickListener {
            if (fullscreen) {
                fullscreenButton.setImageDrawable(ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.ic_baseline_fullscreen_24
                    )
                )
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                if (supportActionBar != null) {
                    supportActionBar!!.show()
                }
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                val params = playerView.layoutParams as LinearLayout.LayoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = (200 * applicationContext.resources.displayMetrics.density).toInt()
                playerView.layoutParams = params
                fullscreen = false
            } else {
                fullscreenButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_baseline_fullscreen_exit_24
                    )
                )
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
                if (supportActionBar != null) {
                    supportActionBar!!.hide()
                }
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                val params = playerView.layoutParams as LinearLayout.LayoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = ViewGroup.LayoutParams.MATCH_PARENT
                playerView.layoutParams = params
                fullscreen = true
                playerView.onPause()
            }
        }


    }

}