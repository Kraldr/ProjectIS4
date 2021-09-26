package com.example.project

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.primera.content.contentClass
import com.example.primera.menu.boolNotify
import com.example.primera.menu.cardStart
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList
import android.widget.AdapterView
import com.example.primera.content.subCategoryClass
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout


private lateinit var dbref : DatabaseReference
private val listCard:MutableList<cardStart> = ArrayList()
private val listTitle:MutableList<String> = ArrayList()
private val subCategory:MutableList<subCategoryClass> = ArrayList()
private lateinit var dialog: Dialog
private var adapters = arrayOf<String?>()
private var UID = ""
private lateinit var txtTitle:EditText
private lateinit var txtIMG:EditText
private lateinit var txtDescrp:EditText
private lateinit var type:AutoCompleteTextView
private val CHANNEL_ID = "channelTest"

class CreateCategory : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_category)

        var btnCrearTipo = findViewById<Button>(R.id.btnCrearTipo)
        var btnRegistro = findViewById<Button>(R.id.btnRegistro)

        setupRecyclerView()

        txtTitle = findViewById<EditText>(R.id.txtTitle)
        txtIMG = findViewById<EditText>(R.id.txtIMG)
        txtDescrp = findViewById<EditText>(R.id.txtDescrips)
        UID = UUID.randomUUID().toString()
        type = findViewById<AutoCompleteTextView>(R.id.typeArchive)


        btnCrearTipo.setOnClickListener {
            val intent = Intent(this, CreateSubCategory::class.java)
            startActivity(intent)

        }

        btnRegistro.setOnClickListener {
            setupArchiType()

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

    private fun saveData (title:String, url:String, descrip:String, typeLocal:String, typeSubTitle:String, typeSelect:String, typeSelectVideo:String) {

        var number = 1

        val database = FirebaseDatabase.getInstance().getReference("content")
        val databaseBool = FirebaseDatabase.getInstance().getReference("boolNotify")
        val content = contentClass(UID, title, descrip,typeLocal, url, typeSubTitle, typeSelect, typeSelectVideo)
        val contentBool = boolNotify(number.toString(), true, type.text.toString(), txtTitle.text.toString())
        database.child(UID).setValue(content).addOnSuccessListener {
            UID = UUID.randomUUID().toString()
            Toast.makeText(this, "Contenido agregado correctamente", Toast.LENGTH_LONG).show()
            databaseBool.child(number.toString()).setValue(contentBool).addOnSuccessListener {
            }
            dialog.hide()
            finish()
        }

    }

    private fun setupArchiType() {
        dbref = FirebaseDatabase.getInstance().getReference("subCategory")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                subCategory.clear()
                var typeSubTitle = ""
                var typeSubID = ""

                if (snapshot.exists()){
                    for (cardSnapshot in snapshot.children){
                        val card = cardSnapshot.getValue(subCategoryClass::class.java)
                        if (card != null) {
                            subCategory.add(card)
                        }
                    }

                    adapters = arrayOf()

                    var idCategory = ""

                    for (i in listCard) {
                        if (i.title == type.text.toString()) {
                            idCategory = i.id
                        }
                    }

                    for (i in subCategory) {
                        if (idCategory == i.category){
                            adapters = append(adapters, i.title)
                        }
                    }

                    MaterialAlertDialogBuilder(this@CreateCategory)
                        .setTitle("Seleccione una subcategoría")
                        .setPositiveButton("Continuar") { _, _ ->
                            if (typeSubID == "") {
                                for (i in subCategory) {
                                    if (adapters[0] == i.title) {
                                        typeSubID = i.id
                                        typeSubTitle = i.title
                                    }
                                }
                            }

                            val typeAr = arrayOf("Video", "Imagen")
                            var typeSelect = ""
                            val title = txtTitle.text.toString()
                            var url = txtIMG.text.toString()
                            val descrip = txtDescrp.text.toString()
                            val typeLocal = type.text.toString()
                            MaterialAlertDialogBuilder(this@CreateCategory)
                                .setTitle("Seleccione el tipo de arhivo")
                                .setPositiveButton("Continuar") { _, _ ->
                                    if (typeSelect == "") {
                                        typeSelect = typeAr[0]
                                    }
                                    if (typeSelect == "Video") {
                                        val typeVideo = arrayOf("YouTube", "Otra plataforma")
                                        var typeSelectVideo = ""
                                        MaterialAlertDialogBuilder(this@CreateCategory)
                                            .setTitle("Seleccione el tipo de arhivo")
                                            .setPositiveButton("Continuar") { _, _ ->
                                                if (typeSelectVideo == "") {
                                                    typeSelectVideo = typeVideo[0]
                                                }

                                                MaterialAlertDialogBuilder(this@CreateCategory)
                                                    .setTitle("Información a cargar")
                                                    .setMessage("UID: $UID\nTitulo: $title\nURL: $url\nDescripción: $descrip\nCategoría: $typeLocal\nSubcategoría: $typeSubTitle\nTipo de archivo: $typeSelect\nPlataforma: $typeSelectVideo")
                                                    .setNegativeButton("Cancelar") { dialog, which ->

                                                    }
                                                    .setPositiveButton("Crear") { dialog, which ->
                                                        loadSesion()
                                                        when {
                                                            url.contains("https://www.youtube.com/watch?v=")-> {
                                                                val lstValues: List<String> = url.split("v=").map { it -> it.trim() }
                                                                url = lstValues[1]
                                                            }
                                                            url.contains("https://youtu.be/") -> {
                                                                val lstValues: List<String> = url.split("be/").map { it -> it.trim() }
                                                                url = lstValues[1]
                                                            }
                                                            else -> {
                                                                url = txtIMG.text.toString()
                                                            }
                                                        }
                                                        saveData(title ,url , descrip, idCategory, typeSubID, typeSelect, typeSelectVideo)
                                                    }
                                                    .show()
                                            }
                                            .setSingleChoiceItems(typeVideo, 0) { dialog, which ->
                                                typeSelectVideo = typeVideo[which]
                                            }.show()
                                    }else {
                                        MaterialAlertDialogBuilder(this@CreateCategory)
                                            .setTitle("Información a cargar")
                                            .setMessage("UID: $UID\nTitulo: $title\nURL: $url\nDescripción: $descrip\nCategoría: $typeLocal\nSubcategoría: $typeSubTitle\nTipo de archivo: $typeSelect")
                                            .setNegativeButton("Cancelar") { dialog, which ->
                                                // Respond to negative button press
                                            }
                                            .setPositiveButton("Crear") { dialog, which ->
                                                loadSesion ()
                                                saveData(title ,url , descrip, idCategory, typeSubID, typeSelect, "")
                                            }
                                            .show()
                                    }
                                }
                                .setSingleChoiceItems(typeAr, 0) { dialog, which ->
                                    typeSelect = typeAr[which]
                                }.show()

                        }
                        .setSingleChoiceItems(adapters, 0) { dialog, which ->
                            for (i in listCard) {
                                if (adapters[which] == i.title) {
                                    typeSubID = i.id
                                    typeSubTitle = i.title
                                }
                            }
                        }.show()


                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    fun append(arr: Array<String?>, element: String): Array<String?> {
        val array = arrayOfNulls<String?>(arr.size + 1)
        System.arraycopy(arr, 0, array, 0, arr.size)
        array[arr.size] = element
        return array
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