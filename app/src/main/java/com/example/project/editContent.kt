package com.example.project

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.primera.content.contentClass
import com.example.primera.menu.cardStart
import com.example.project.R
import com.google.firebase.database.*
import kotlin.collections.ArrayList
private lateinit var dbref : DatabaseReference
private val listCard:MutableList<cardStart> = ArrayList()
private val list:MutableList<contentClass> = ArrayList()
private val listTitle:MutableList<String> = ArrayList()
private lateinit var dialog: Dialog
private var meesage:String = ""
private var UID:String = ""
private var type:String = ""

class editContent : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_content)

        var btnActualizar = findViewById<Button>(R.id.btnActualizar)
        meesage = intent.getStringExtra("key").toString()


        var typeArchive = findViewById<EditText>(R.id.typeArchive)

        setupRecyclerView()
        setupContent()



        btnActualizar.setOnClickListener {
            loadSesion()
            if (typeArchive.text.toString() != "" && typeArchive.text.toString() != type) {
                saveData(typeArchive.text.toString())
            }else {
                saveData(type)
            }
        }


    }

    private fun loadSesion () {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_progress_bar_with_crear)
        dialog.show()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }

    private fun saveData (types: String) {
        var txtTitle = findViewById<EditText>(R.id.txtTitle)
        var txtIMG = findViewById<EditText>(R.id.txtIMG)
        var txtDescrp = findViewById<EditText>(R.id.txtDescrips)


        val database = FirebaseDatabase.getInstance().getReference("content")
        val content = contentClass(UID, txtTitle.text.toString(), txtDescrp.text.toString(),types, txtIMG.text.toString())
        database.child(UID).setValue(content).addOnSuccessListener {
            Toast.makeText(this, "Contenido actualizado correctamente", Toast.LENGTH_LONG).show()
            dialog.hide()
            finish()
        }

    }

    private fun setupContent() {
        dbref = FirebaseDatabase.getInstance().getReference("content")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()

                if (snapshot.exists()){

                    for (cardSnapshot in snapshot.children){
                        val content = cardSnapshot.getValue(contentClass::class.java)
                        if (content != null) {
                            list.add(content)
                        }
                    }
                    datos(list)

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun datos(listCard: MutableList<contentClass>) {
        var txtTitle = findViewById<EditText>(R.id.txtTitle)
        var txtIMG = findViewById<EditText>(R.id.txtIMG)
        var txtDescrp = findViewById<EditText>(R.id.txtDescrips)

        for (i in listCard) {
            if (i.id == meesage) {
                txtTitle.setText(i.title)
                txtIMG.setText(i.url)
                txtDescrp.setText(i.descrip)
                UID = i.id
                type = i.type
            }
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