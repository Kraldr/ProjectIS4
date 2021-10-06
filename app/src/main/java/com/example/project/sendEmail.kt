package com.example.project

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import com.example.primera.content.subCategoriesClass
import com.example.primera.menu.cardStart
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*

private lateinit var dbref : DatabaseReference
private val listCard:MutableList<cardStart> = ArrayList()
private val listTitle:MutableList<String> = ArrayList()
private lateinit var name: String
private lateinit var correo: String
private var subCa: String? = ""
private lateinit var categories:AutoCompleteTextView
private val SUB_CATEGORIES:MutableList<subCategoriesClass> = ArrayList()
private lateinit var prop :AutoCompleteTextView
private lateinit var  email :EditText
private lateinit var  txtNombre :EditText
private lateinit var  txtDescrips :EditText
private lateinit var  btnEnviar :Button


class sendEmail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_email)

        prop = findViewById<AutoCompleteTextView>(R.id.type)
        email = findViewById<EditText>(R.id.txtEmail)
        txtNombre = findViewById<EditText>(R.id.txtNombre)
        txtDescrips = findViewById<EditText>(R.id.txtDescrips)
        btnEnviar = findViewById<Button>(R.id.btnRegistro)

        val list = resources.getStringArray(R.array.typeProp)
        val adapters = ArrayAdapter(applicationContext, R.layout.list_item, list)
        prop.setAdapter(adapters)

        val sharedPreferences: SharedPreferences = getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
        name = sharedPreferences.getString("name", null).toString()
        correo = sharedPreferences.getString("correo", null).toString()

        txtNombre.setText(name)
        email.setText(correo)

        setupRecyclerView()

        btnEnviar.setOnClickListener{
            setupRecyclerSub()
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
                            categories = findViewById<AutoCompleteTextView>(R.id.typeAccount)
                            categories.setAdapter(adapters)
                        }
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun setupRecyclerSub() {
        dbref = FirebaseDatabase.getInstance().getReference("subCategory")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                SUB_CATEGORIES.clear()

                if (snapshot.exists()){

                    for (cardSnapshot in snapshot.children){
                        val card = cardSnapshot.getValue(subCategoriesClass::class.java)
                        if (card != null) {
                            SUB_CATEGORIES.add(card)
                        }
                    }

                    dbref = FirebaseDatabase.getInstance().getReference("ArchiType")
                    dbref.addValueEventListener(object : ValueEventListener {

                        override fun onDataChange(snapshot: DataSnapshot) {
                            listCard.clear()

                            if (snapshot.exists()){

                                for (cardSnapshot in snapshot.children){
                                    val card = cardSnapshot.getValue(cardStart::class.java)
                                    if (card != null) {
                                        listCard.add(card)
                                    }
                                }

                                var adapters = arrayOf<String?>()
                                adapters = arrayOf()

                                for (i in listCard) {
                                    if (i.title == categories.text.toString() && !categories.text.toString().isEmpty()) {
                                        for (j in SUB_CATEGORIES) {
                                            if (j.category == i.id) {
                                                adapters = append(adapters, j.title)
                                            }
                                        }
                                    }
                                }

                                adapters = append(adapters, "Sin subcategoría")

                                MaterialAlertDialogBuilder(this@sendEmail)
                                    .setTitle("Seleccione el tipo de arhivo")
                                    .setPositiveButton("Continuar") { _, _ ->
                                        if (subCa!!.isEmpty()) {
                                            subCa = adapters[0]
                                        }
                                        sendMail()
                                    }
                                    .setSingleChoiceItems(adapters, 0) { dialog, which ->
                                        subCa = adapters[which]
                                    }.show()

                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })

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

    private fun sendMail() {
        //Send Mail
        val em = email.text.toString()
        val javaMailAPI = JavaMailAPI(this, "amedina588@soyudemedellin.edu.co",  name + " a enviado " +
                prop.text.toString() + " de " + categories.text.toString() + "(${subCa})", " /**  Correo de respuesta: $em  **/\n\n"
                + "Tipo de solicitud: " + prop.text.toString() + "\n" + "Categoría: " + categories.text.toString() + "\n"
                + "Subcategoría: " + subCa + "\n\n\n" + txtDescrips.text.toString())
        javaMailAPI.execute()
    }
}