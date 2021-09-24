package com.example.project

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.project.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var type: String

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val sharedPreferences: SharedPreferences = getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
        type = sharedPreferences.getString("type", null).toString()

        supportActionBar?.elevation = 0F

        val toolbar: androidx.appcompat.widget.Toolbar = binding.appBarMain.toolbar
        toolbar.title = ""
        binding.appBarMain.toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"))
        binding.appBarMain.toolbar.title = "Title"
        val drawer: DrawerLayout = findViewById(R.id.drawer_layout)

        toolbar.post() {
            val d = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_menu_24black, null)
            toolbar.navigationIcon = d
            toolbar.elevation = 0F
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout

        drawerLayout.elevation = 0F

        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val item = menu.findItem(R.id.ids)
        if (type != "Organizador") {
            item.isVisible = false
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            /*R.id.item -> {
                val intent = Intent(this, MainActivity::class.java).apply {
                }
                saveData("sincorreo",false,"falso")
                Toast.makeText(this, "Sesión Cerrada", Toast.LENGTH_LONG).show()
                startActivity(intent)
                val myService = Intent(this@MainActivity, MyService::class.java)
                stopService(myService)
                finish()
            }*/

            R.id.ids -> {
                val intent = Intent(this, CreateCategory::class.java).apply {
                }
                startActivity(intent)
            }
            /*R.id.infoAdd -> {
                val intent = Intent(this, infoAdd::class.java).apply {
                }
                startActivity(intent)
            }*/

        }
        return super.onOptionsItemSelected(item)
    }


    private fun saveData (correo:String, online:Boolean, type: String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putString("correo", correo)
            putString("type", type)
            putBoolean("online", online)
        }.apply()
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun getConext (): Context {
        return this@MainActivity
    }
}