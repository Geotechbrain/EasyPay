package com.geodeveloper.easypay.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.geodeveloper.easypay.Constants
import com.geodeveloper.easypay.R
import com.geodeveloper.easypay.adapter.ServiceAdapter
import com.geodeveloper.easypay.model.UsersModel
import com.geodeveloper.easypay.models.airtime.Airtime
import com.geodeveloper.easypay.service.ApiService
import com.geodeveloper.easypay.service.ServiceBuilder
import com.geodeveloper.paybills.helper.Utils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_buy_data.*
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    private var drawerLayout: DrawerLayout? = null
    private var navigatioView: NavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        toolbar = supportActionBar!!
//        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)*/
        
        //set toobar
        setSupportActionBar(app_bar)
        setupNavigation()


        //val topAppBar: androidx.appcompat.widget.Toolbar = findViewById(R.id.app_bar)
        ///setSupportActionBar(topAppBar)


        //for airtime list
        main_airtimeList.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        main_airtimeList.layoutManager = layoutManager

        //for data list
        main_dataList.setHasFixedSize(true)
        val dataLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        main_dataList.layoutManager = dataLayoutManager

        //for education list
        main_educationList.setHasFixedSize(true)
        val eduLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        main_educationList.layoutManager = eduLayoutManager

        //for tvCable list
        main_tvCableList.setHasFixedSize(true)
        val tvLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        main_tvCableList.layoutManager = tvLayoutManager

        //for electriciy list
        main_electricityList.setHasFixedSize(true)
        val electricityLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        main_electricityList.layoutManager = electricityLayoutManager

        getServices().execute()

        FirebaseAuth.getInstance().addAuthStateListener {
           if (it.currentUser == null) {
               startActivity(Intent(this,LoginActivity()::class.java))
               finish()
           }
        }
        main_fund_wallet.setOnClickListener {
            startActivity(Intent(this,FundwalletActivity::class.java))
        }
    }

    private fun setupNavigation() {
        drawerLayout = findViewById(R.id.main_drawer)
        navigatioView = findViewById(R.id.main_nav_view)
        navigatioView!!.setNavigationItemSelectedListener(this)
        val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, app_bar, R.string.drawer_open, R.string.drawer_close)
        drawerLayout!!.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
    }
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.home -> {
            //TODO
            }
        }

        closeDrawer()
        return false
    }

    private fun closeDrawer() {
        drawerLayout!!.closeDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START))
            closeDrawer()
        else {
            super.onBackPressed()
        }
    }

    private fun getAirtimeList() {
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.getAirtimeService("airtime")
        requestCall.enqueue(object : Callback<Airtime> {
            override fun onResponse(call: Call<Airtime>, response: Response<Airtime>) {
                if (response.isSuccessful) {
                    val itemLists = response.body()!!
                    main_airtimeList.adapter = ServiceAdapter(this@MainActivity, itemLists, Constants.airtime)
                } else {
                    Toast.makeText(this@MainActivity, "error occur", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Airtime>, t: Throwable) {
                Toast.makeText(this@MainActivity, "error occur", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getDataList() {
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.getAirtimeService("data")
        requestCall.enqueue(object : Callback<Airtime> {
            override fun onResponse(call: Call<Airtime>, response: Response<Airtime>) {
                if (response.isSuccessful) {
                    val itemLists = response.body()!!
                    main_dataList.adapter = ServiceAdapter(this@MainActivity, itemLists, Constants.data)
                } else {
                    Toast.makeText(this@MainActivity, "error occur", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Airtime>, t: Throwable) {
                Toast.makeText(this@MainActivity, "error occur", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getEducationList() {
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.getAirtimeService("education")
        requestCall.enqueue(object : Callback<Airtime> {
            override fun onResponse(call: Call<Airtime>, response: Response<Airtime>) {
                if (response.isSuccessful) {
                    val itemLists = response.body()!!
                    main_educationList.adapter = ServiceAdapter(this@MainActivity, itemLists, Constants.education)
                } else {
                    Toast.makeText(this@MainActivity, "error occur", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Airtime>, t: Throwable) {
                Toast.makeText(this@MainActivity, "error occur", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getTVCableList() {
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.getAirtimeService("tv-subscription")
        requestCall.enqueue(object : Callback<Airtime> {
            override fun onResponse(call: Call<Airtime>, response: Response<Airtime>) {
                if (response.isSuccessful) {
                    val itemLists = response.body()!!
                    main_tvCableList.adapter = ServiceAdapter(this@MainActivity, itemLists, Constants.tv)
                } else {
                    Toast.makeText(this@MainActivity, "error occur", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Airtime>, t: Throwable) {
                Toast.makeText(this@MainActivity, "error occur", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getElectricity() {
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.getAirtimeService("electricity-bill")
        requestCall.enqueue(object : Callback<Airtime> {
            override fun onResponse(call: Call<Airtime>, response: Response<Airtime>) {
                if (response.isSuccessful) {
                    val itemLists = response.body()!!
                    main_electricityList.adapter = ServiceAdapter(this@MainActivity, itemLists, Constants.electricity)
                } else {
                    Toast.makeText(this@MainActivity, "error occur", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Airtime>, t: Throwable) {
                Toast.makeText(this@MainActivity, "error occur", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun loadCurrentUserWalletBalance() {
        Utils.databaseRef().child(Constants.users).child(Utils.currentUserID()).child("wallet_balance").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    main_walletAmount.text = "NGN ${p0.value.toString().toDouble()}"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    inner class getServices : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg p0: Void?): Void? {
            loadCurrentUserWalletBalance()
            getAirtimeList()
            getDataList()
            getEducationList()
            getTVCableList()
            getElectricity()
            return null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_appbar_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
           R.id.logout->
           {
               FirebaseAuth.getInstance().signOut()
               val intent = Intent(this, OnBoardActivity::class.java)
               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
               startActivity(intent)
           }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        setUserBasicInfo()
    }

    private fun setUserBasicInfo() {
        Utils.databaseRef().child(Constants.users).child(Utils.currentUserID()).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(UsersModel::class.java)
                val pref = getSharedPreferences("USERPREF", Context.MODE_PRIVATE)
                pref.edit {
                    putString("userfullname", user!!.fullname)
                    putString("useremail",user.email)
                    apply()
                }
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}
