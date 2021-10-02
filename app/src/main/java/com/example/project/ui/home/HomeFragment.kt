package com.example.project.ui.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.contentClass
import com.example.primera.menu.boolNotify
import com.example.primera.menu.cardStart
import com.example.primera.menu.card_menu_lis_adapter
import com.example.project.card_top_adapter
import com.example.project.MainActivity
import com.example.project.MyService
import com.example.project.R
import com.example.project.databinding.FragmentHomeBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var menuAll: Menu
    private lateinit var type: String

    private val database = Firebase.database
    private lateinit var dbref : DatabaseReference
    private lateinit var messagesListener: ValueEventListener
    private lateinit var saveEmail: String
    private val listCard:MutableList<cardStart> = ArrayList()
    private val listCardTop:MutableList<contentClass> = ArrayList()
    private val listBool:MutableList<boolNotify> = ArrayList()
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    val myRef = database.getReference("cards")
    private lateinit var item: MenuItem

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
        saveEmail = sharedPreferences.getString("correo", null).toString()
        type = sharedPreferences.getString("type", null).toString()
        val recycler = root.findViewById<RecyclerView>(R.id.listRecycler)
        val recyclerTop = root.findViewById<RecyclerView>(R.id.listRecyclerTop)
        setupRecyclerView(recycler)
        setupRecyclerViewTop(recyclerTop)


        val myService = Intent(requireActivity().applicationContext, MyService::class.java)
        myService.putExtra("inputExtra", "Cosa");
        requireActivity().startService(myService)
        setupBoolNotify ()

        try {
            (activity as MainActivity?)!!.configToolbar()
        }catch (e: Exception) {

        }

        return root
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
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

                    datos(recyclerView, listCard)

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun setupRecyclerViewTop(recyclerView: RecyclerView) {
        dbref = FirebaseDatabase.getInstance().getReference("content")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                listCardTop.clear()

                if (snapshot.exists()){

                    for (cardSnapshot in snapshot.children){
                        val content = cardSnapshot.getValue(contentClass::class.java)
                        if (content != null) {
                            listCardTop.add(content)
                        }
                    }

                    datosTop(recyclerView, listCardTop)

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun datosTop (recycler:RecyclerView, all: MutableList<contentClass>) {
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
        val types = sharedPreferences.getString("type", null).toString()
        val context: Context = (activity as MainActivity?)!!.getConext()
        recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = card_top_adapter(all, context, types)
        }

    }

    private fun datos (recycler:RecyclerView, all: MutableList<cardStart>) {
        recycler.apply {
            layoutManager = LinearLayoutManager(requireActivity().applicationContext)
            adapter = card_menu_lis_adapter(all, type, requireActivity().applicationContext)
        }

        recycler.layoutManager = LinearLayoutManager(requireActivity().applicationContext, RecyclerView.HORIZONTAL,false)
    }


    private fun setupBoolNotify () {
        dbref = FirebaseDatabase.getInstance().getReference("boolNotify")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                listBool.clear()

                if (snapshot.exists()){

                    for (boolNotifySnapshot in snapshot.children){
                        val boolNotifys = boolNotifySnapshot.getValue(boolNotify::class.java)
                        if (boolNotifys != null) {
                            listBool.add(boolNotifys)
                        }
                    }

                    if (listBool[0].boolNoti) {
                        val myService = Intent(requireActivity().applicationContext, MyService::class.java)
                        requireActivity().startService(myService)
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }


    /*private fun startList() {
        val intent = Intent(this, Inicio::class.java).apply {

        }
        startActivity(intent)
        finish()
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}