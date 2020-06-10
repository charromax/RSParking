package com.example.rsparking.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.rsparking.R
import com.example.rsparking.databinding.ActivityLoginScreenBinding
import com.example.rsparking.databinding.ActivityLoginScreenBindingImpl
import com.example.rsparking.ui.mainactivity.MainActivity
import com.example.rsparking.util.Constants

class LoginScreen : AppCompatActivity() {
    private lateinit var binding: ActivityLoginScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_login_screen)
    }

    override fun onStart() {
        super.onStart()
            binding.btnSubmit.setOnClickListener{
            val usr= binding.txtLogin.text.toString().trim()
            val pwd= binding.txtPass.text.toString().trim()
            Constants.USR_LEVEL= usr.toLowerCase()

            if (usr.equals("admin", ignoreCase = true) && pwd.equals(
                    "admin", ignoreCase = true))
            {
                Toast.makeText(this, "Welcome, Balta", Toast.LENGTH_SHORT).show()
                val i = Intent(this, MainActivity::class.java)
                i.putExtra(Constants.TAG_LOGIN, "admin")
                startActivity(i)
                this.finish()

            } else if (usr.equals("driver", ignoreCase = true) && pwd.equals(
                    "driver", ignoreCase = true))
            {
                Toast.makeText(this, "Welcome, Driver", Toast.LENGTH_SHORT).show()
                val i = Intent(this, MainActivity::class.java)
                i.putExtra(Constants.TAG_LOGIN, "driver")
                startActivity(i)
                this.finish()
            } else {
                Toast.makeText(this, "Login Error", Toast.LENGTH_SHORT).show()
                binding.txtLogin.setText("")
                binding.txtPass.setText("")
                binding.txtLogin.requestFocus()
            }
        }
    }
}
