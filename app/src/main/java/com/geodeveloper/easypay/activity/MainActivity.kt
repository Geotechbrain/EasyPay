package com.geodeveloper.easypay.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.geodeveloper.easypay.Constants
import com.geodeveloper.easypay.R
import com.geodeveloper.easypay.fragment.ContactUsFragment
import com.geodeveloper.easypay.fragment.HistoryFragment
import com.geodeveloper.easypay.fragment.HomeFragment
import com.geodeveloper.easypay.fragment.ProfileFragment
import com.geodeveloper.easypay.model.UsersModel
import com.geodeveloper.easypay.helper.Utils
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.customerly.Customerly
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    private var selectedFragment: Fragment? = null
    private var drawerLayout: DrawerLayout? = null
    private var navigatioView: NavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.main_container, HomeFragment()).commit()
//        toolbar = supportActionBar!!
//        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)*/
        
        //set toobar
        setSupportActionBar(app_bar)
        setupNavigation()


        //val topAppBar: androidx.appcompat.widget.Toolbar = findViewById(R.id.app_bar)
        ///setSupportActionBar(topAppBar)


        //for airtime list

        FirebaseAuth.getInstance().addAuthStateListener {
           if (it.currentUser == null) {
               startActivity(Intent(this,LoginActivity()::class.java))
               finish()
           }
        }

        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home -> selectedFragment = HomeFragment()
                R.id.transaction -> selectedFragment = HistoryFragment()
                R.id.help -> selectedFragment = ContactUsFragment()
                R.id.profile -> selectedFragment = ProfileFragment()
            }
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction().replace(R.id.main_container, selectedFragment!!).commit()
            }
            return@setOnNavigationItemSelectedListener  true
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
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_container, HomeFragment()).commit()
            }
            R.id.nav_transact ->  supportFragmentManager.beginTransaction().replace(R.id.main_container, HistoryFragment()).commit()
            R.id.nav_help ->  supportFragmentManager.beginTransaction().replace(R.id.main_container, ContactUsFragment()).commit()
            R.id.nav_profile ->  supportFragmentManager.beginTransaction().replace(R.id.main_container, ProfileFragment()).commit()
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
                Customerly.configure(application, "a615c2fe")
                Customerly.registerUser(user!!.email, user.user_id, user.fullname, null, null, {}, {})
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}
