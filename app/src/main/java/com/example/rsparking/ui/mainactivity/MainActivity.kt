package com.example.rsparking.ui.mainactivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.rsparking.R
import com.example.rsparking.databinding.ActivityMainBinding
import com.example.rsparking.util.ToolbarInterface
import com.google.android.material.navigation.NavigationView

const val TAG= "MainActivity"

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    ToolbarInterface {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        toolbar = binding.toolbar
        drawerLayout = binding.drawerLayout
        navView = binding.navView
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            Navigation.findNavController(this, R.id.nav_host),
            drawerLayout
        )
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.setChecked(true)
        when (item.itemId) {
            R.id.nav_drivers -> {
                this.findNavController(R.id.nav_host).navigate(R.id.driverListFragment)
            }
            R.id.nav_clients -> {
                this.findNavController(R.id.nav_host).navigate(R.id.clientListFragment)
            }
            R.id.nav_pickups -> {
                this.findNavController(R.id.nav_host).navigate(R.id.dropOffListFragment)
            }
            R.id.nav_history -> {
                this.findNavController(R.id.nav_host).navigate(R.id.pastDropOffsListFragment)
            }
            R.id.nav_reports -> {
                Log.i(TAG, "onNavigationItemSelected: drivers")
            }
            R.id.nav_exit -> {
                this.finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun getToolbarResources(title: String, visibility: Int) {
        supportActionBar?.title = title
    }
}
