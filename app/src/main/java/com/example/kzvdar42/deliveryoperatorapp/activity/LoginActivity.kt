package com.example.kzvdar42.deliveryoperatorapp.activity


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.kzvdar42.deliveryoperatorapp.R
import com.example.kzvdar42.deliveryoperatorapp.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private val mViewModel
            by lazy { ViewModelProviders.of(this).get(LoginViewModel::class.java) }
    private val sharedPref
            by lazy { getSharedPreferences("user", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Add the toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.login_label)
        setSupportActionBar(toolbar)

        // Go to main page if already logged in.
        if (sharedPref.getString("token", "") != "") goToMainPage()
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.login_button -> {
                login()
            }
        }
    }

    private fun login() {
        val login = setUsername.text.toString()
        val password = setPassword.text.toString()
        if (validate()) {
            val result = mViewModel.login(login, password)
            result.first.observe(this, Observer<String> { response ->
                if (response == "OK") {
                    goToMainPage()
                } else {
                    Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun goToMainPage() {
        intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(intent)
    }

    private fun validate(): Boolean {
        var valid = true

        val login = setUsername.text.toString()
        val password = setPassword.text.toString()

        if (login.isEmpty() || login.length < 6) {
            setUsername.error = getString(R.string.username_error)
            valid = false
        } else {
            setUsername.error = null
        }

        if (password.isEmpty() || password.length < 6) {
            setPassword.error = getString(R.string.password_error)
            valid = false
        } else {
            setPassword.error = null
        }

        return valid
    }
}
