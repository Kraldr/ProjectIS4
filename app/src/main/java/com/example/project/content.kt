package com.example.project

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.primera.content.contentClass
import com.google.firebase.database.*

private lateinit var dbref : DatabaseReference
private val listCard:MutableList<contentClass> = ArrayList()
private var meesage:String = ""
private lateinit var dialog: Dialog
private lateinit var type: String

class content : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)

        val sharedPreferences: SharedPreferences = getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
        type = sharedPreferences.getString("type", null).toString()

        meesage = intent.getStringExtra("Type").toString()
        val recycler = findViewById<RecyclerView>(R.id.recyclerContent)
        loadSesion()


        setupRecyclerView(recycler)
    }

    private fun loadSesion () {

        dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_progress_bar_with_text)
        //dialog.show()
        //dialog.setCancelable(false)
        //dialog.setCanceledOnTouchOutside(false)
    }

    /*override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.context_menu, menu)
    }*/


    private fun setupRecyclerView(recyclerView: RecyclerView) {
        dbref = FirebaseDatabase.getInstance().getReference("content")
        val listCards:MutableList<contentClass> = ArrayList()
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                listCard.clear()
                listCards.clear()

                if (snapshot.exists()){

                    for (cardSnapshot in snapshot.children){
                        val content = cardSnapshot.getValue(contentClass::class.java)
                        if (content != null) {
                            listCard.add(content)
                        }
                    }

                    for (i in listCard) {
                        if(i.type == meesage) {
                            listCards.add(i)
                        }
                    }

                    datos(recyclerView, listCards)

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun datos (recycler:RecyclerView, all: MutableList<contentClass>) {
        recycler.apply {
            layoutManager = LinearLayoutManager(this@content)
            adapter = content_lis_adapter(all,this@content, type)
        }

        recycler.layoutManager = GridLayoutManager(this, 1)
    }
}