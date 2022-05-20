package com.kushagra.restaurent

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import fragment.FavouriteFragment
import fragment.HistoryFragment
import fragment.HomeFragment
import fragment.ProfileFragment

class MainActivity : AppCompatActivity()
{
    lateinit var DrawerLayout : DrawerLayout
    lateinit var coordinator : CoordinatorLayout
    lateinit var AppBar : AppBarLayout
    lateinit var ToolBar : androidx.appcompat.widget.Toolbar
    lateinit var FrameLayout : FrameLayout
    lateinit var Navigation : NavigationView
    lateinit var sharedPreferences: SharedPreferences

    lateinit var sharedPreferences_for_profile: SharedPreferences       // print the name in header logic       //-
    lateinit var headerView : View                          // print the name in header logic                                   //-
    lateinit var header_name : TextView     // print the name in header logic
    lateinit var header_email : TextView    // print the name in header logic

    var uname : String? = null              //-
    var email : String? = null                //-

    var chack : MenuItem?  = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE) // use to logout
        DrawerLayout = findViewById(R.id.DrawerLayout)
        coordinator = findViewById(R.id.coordinator)
        AppBar = findViewById(R.id.AppBar)
        ToolBar = findViewById(R.id.ToolBar)
        FrameLayout = findViewById(R.id.FrameLayout)
        Navigation = findViewById(R.id.Navigation)

        headerView = Navigation.getHeaderView(0)                   //-
        header_name = headerView.findViewById(R.id.header_name)         //-
        header_email = headerView.findViewById(R.id.header_email)       //-

        sharedPreferences_for_profile = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        uname = sharedPreferences_for_profile.getString("name1", "defaultdata")
        email = sharedPreferences_for_profile.getString("email", "defaultdata")

        header_name.text = uname
        header_email.text = email
        setUpToolBar()
        openHomePage()


        val actionBarDrawerToggle = ActionBarDrawerToggle(        // it is also use to make ham. icon
            this@MainActivity, DrawerLayout, R.string.open_drawer, R.string.close_drawer)
        DrawerLayout.addDrawerListener(actionBarDrawerToggle)     // use to make a hamburger icon on toolbar
        actionBarDrawerToggle.syncState()

        Navigation.setNavigationItemSelectedListener {

             if(chack != null)
            {
                 chack?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            chack = it

            when(it.itemId)
            {
                R.id.Home ->
                {
                    openHomePage()
                    DrawerLayout.closeDrawers()
                }

                R.id.Favourite ->
                {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.FrameLayout, FavouriteFragment())
                        .commit()
                    DrawerLayout.closeDrawers()
                    supportActionBar?.title = "Favourites"
                    Toast.makeText(this@MainActivity, "Clicked on Favorites", Toast.LENGTH_SHORT).show()
                }

                R.id.Profile ->
                {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.FrameLayout, ProfileFragment())
                        .commit()
                    DrawerLayout.closeDrawers()
                    supportActionBar?.title = "Profile"
                    Toast.makeText(this@MainActivity, "Clicked on Profile", Toast.LENGTH_SHORT).show()
                }

                R.id.history ->
                {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.FrameLayout, HistoryFragment())
                        .commit()
                    DrawerLayout.closeDrawers()
                    supportActionBar?.title = "Order History"
                    Toast.makeText(this@MainActivity, "Clicked on History", Toast.LENGTH_SHORT).show()
                }

                R.id.LogOut ->
                {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you Sure, you want to log out")
                    dialog.setPositiveButton("yes")
                    { text, listener -> val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        sharedPreferences.edit().clear().apply()
                        startActivity(intent)
                        this.finish()
                    }

                    dialog.setNegativeButton("No")
                    {
                            text, listener -> dialog.setCancelable(true)
                    }
                    dialog.create()
                    dialog.show()
                }

            }

            return@setNavigationItemSelectedListener true
        }

    }

    fun setUpToolBar()
    {
      setSupportActionBar(ToolBar)
      supportActionBar?.title = "Home Page"
      supportActionBar?.setHomeButtonEnabled(true)
      supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun openHomePage()
    {
        supportFragmentManager.beginTransaction()
            .replace(R.id.FrameLayout,HomeFragment())
            .commit()
        supportActionBar?.title = "All Restaurants"
        Navigation.setCheckedItem(R.id.Home)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean     //use to open a drawer when click on hamburger icon
    {
        val id = item.itemId
        if(id == android.R.id.home)
        {
            DrawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed()
    {
        if(this.DrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            this.DrawerLayout.closeDrawer(GravityCompat.START)
        }

        else {

            val frag = supportFragmentManager.findFragmentById(R.id.FrameLayout)
            when (frag) {
                !is HomeFragment -> openHomePage()

                else -> super.onBackPressed()
            }
        }
    }
} // class closed