package com.example.project

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.example.primera.content.contentClass
import com.example.primera.menu.boolNotify
import com.example.primera.menu.cardStart
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

private lateinit var dbref : DatabaseReference
private val listCard:MutableList<cardStart> = ArrayList()
private val listTitle:MutableList<String> = ArrayList()
private lateinit var dialog: Dialog
private val CHANNEL_ID = "channelTest"

class CreateCategory : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_category)

        var btnCrearTipo = findViewById<Button>(R.id.btnCrearTipo)
        var btnRegistro = findViewById<Button>(R.id.btnRegistro)

        setupRecyclerView()


        btnCrearTipo.setOnClickListener {
            val intent = Intent(this, CreateSubCategory::class.java)
            startActivity(intent)

        }

        btnRegistro.setOnClickListener {
            loadSesion()
            saveData()
        }

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

    private fun loadSesion () {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_progress_bar_with_crear)
        dialog.show()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }

    private fun saveData () {
        var txtTitle = findViewById<EditText>(R.id.txtTitle)
        var txtIMG = findViewById<EditText>(R.id.txtIMG)
        var txtDescrp = findViewById<EditText>(R.id.txtDescrips)
        var UID = UUID.randomUUID().toString()
        val type = findViewById<AutoCompleteTextView>(R.id.typeArchive)

        var number = 1
        var types = ""
        var titleType = ""

        for (i in listCard) {
            if (type.text.toString() == i.title) {
                types = i.id
            }
        }

        val database = FirebaseDatabase.getInstance().getReference("content")
        val databaseBool = FirebaseDatabase.getInstance().getReference("boolNotify")
        val content = contentClass(UID, txtTitle.text.toString(), txtDescrp.text.toString(),types, txtIMG.text.toString())
        val contentBool = boolNotify(number.toString(), true, type.text.toString(), txtTitle.text.toString())
        database.child(UID).setValue(content).addOnSuccessListener {
            Toast.makeText(this, "Contenido agregado correctamente", Toast.LENGTH_LONG).show()
            databaseBool.child(number.toString()).setValue(contentBool).addOnSuccessListener {
            }
            dialog.hide()
            finish()
        }

    }

    private fun setupRecyclerView() {
        dbref = FirebaseDatabase.getInstance().getReference("ArchiType")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                listCard.clear()

                if (snapshot.exists()){

                    for (cardSnapshot in snapshot.children){
                        val card = cardSnapshot.getValue(cardStart::class.java)
                        if (card != null) {
                            listCard.add(card)
                            listTitle.clear()
                            for (i in listCard) {
                                listTitle.add(i.title)
                            }
                            val adapters = ArrayAdapter(applicationContext, R.layout.list_item, listTitle)
                            val text = findViewById<AutoCompleteTextView>(R.id.typeArchive)
                            text.setAdapter(adapters)
                        }
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

}