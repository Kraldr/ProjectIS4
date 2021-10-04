package com.example.project

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.*
import com.example.primera.content.subCategoriesClass
import com.example.primera.menu.cardStart
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

private lateinit var uniqueID: kotlin.String
private lateinit var type: kotlin.String
private lateinit var dialog: Dialog
private lateinit var dbref : DatabaseReference
private val listCard:MutableList<cardStart> = ArrayList()
private var adapters = arrayOf<String?>()
private val listTitle:MutableList<String> = ArrayList()

class CreateSubCategory : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_sub_category)

        try {
            var uniqueID = UUID.randomUUID().toString()

            var txtUID = findViewById<EditText>(R.id.txtIDType)
            var btnSubir = findViewById<Button>(R.id.btnRegistrar)
            val list = resources.getStringArray(R.array.typeCategory)
            val adapters = ArrayAdapter(applicationContext, R.layout.list_item, list)
            val text = findViewById<AutoCompleteTextView>(R.id.typeCategory)
            text.setAdapter(adapters)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.statusBarColor = Color.WHITE;

            txtUID.setText(uniqueID)

            btnSubir.setOnClickListener {
                loadSesion()
                var txtTitle = findViewById<EditText>(R.id.txtTitle)
                var txtUrl = findViewById<EditText>(R.id.txtURLI)
                var txtTypeCategory = findViewById<EditText>(R.id.typeCategory)

                if (txtTitle.text.toString() == ""){

                }else if(txtUrl.text.toString() == "") {

                }else if(txtTypeCategory.text.toString() == "") {

                }else{
                    if (txtTypeCategory.text.toString() == "Categoría") {
                        crearType()
                    }else if (txtTypeCategory.text.toString() == "Subcategoría"){
                        setupArchiType()
                    }
                }
            }
        }catch (e: Exception) {

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

    private fun crearType () {
        var txtUID = findViewById<EditText>(R.id.txtIDType)
        var txtTitle = findViewById<EditText>(R.id.txtTitle)
        var txtUrl = findViewById<EditText>(R.id.txtURLI)
        var UID = txtUID.text.toString()


        val database = FirebaseDatabase.getInstance().getReference("ArchiType")
        val cards = cardStart(UID, txtTitle.text.toString(), txtUrl.text.toString())
        database.child(UID).setValue(cards).addOnSuccessListener {
            Toast.makeText(this, "Tipo de arcivo creado correctamente", Toast.LENGTH_LONG).show()
            dialog.hide()
            finish()
        }
    }

    private fun createSub (type: String) {
        var txtUID = findViewById<EditText>(R.id.txtIDType)
        var txtTitle = findViewById<EditText>(R.id.txtTitle)
        var txtUrl = findViewById<EditText>(R.id.txtURLI)
        var UID = txtUID.text.toString()


        val database = FirebaseDatabase.getInstance().getReference("subCategory")
        val subCategoryClass = subCategoriesClass(UID, txtTitle.text.toString(), txtUrl.text.toString(), type)
        database.child(UID).setValue(subCategoryClass).addOnSuccessListener {
            Toast.makeText(this, "Subcategoría creada correctamente", Toast.LENGTH_LONG).show()
            dialog.hide()
            finish()
        }
    }

    private fun setupArchiType() {
        dbref = FirebaseDatabase.getInstance().getReference("ArchiType")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                listCard.clear()
                var type = ""

                if (snapshot.exists()){
                    for (cardSnapshot in snapshot.children){
                        val card = cardSnapshot.getValue(cardStart::class.java)
                        if (card != null) {
                            listCard.add(card)
                        }
                    }

                    adapters = arrayOf()

                    for (i in listCard) {
                        adapters = append(adapters, i.title)
                    }

                    MaterialAlertDialogBuilder(this@CreateSubCategory)
                        .setTitle("Seleccione una categoría")
                        .setPositiveButton("Crear") { _, _ ->
                            if (type == "") {
                                for (i in listCard) {
                                    if (adapters[0] == i.title) {
                                        type = i.id
                                    }
                                }
                            }
                            createSub(type)
                        }
                        .setSingleChoiceItems(adapters, 0) { dialog, which ->
                            for (i in listCard) {
                                if (adapters[which] == i.title) {
                                    type = i.id
                                }
                            }
                        }.setCancelable(false).show()


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
}