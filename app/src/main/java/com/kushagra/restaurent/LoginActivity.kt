package com.kushagra.restaurent

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import fragment.HomeFragment
import model.Food
import model.UserCredential
import org.json.JSONException
import org.json.JSONObject
import util.ConnectionManager


class LoginActivity : AppCompatActivity()
{

    lateinit var txtRegister: TextView
    lateinit var txtForgot: TextView
    lateinit var btnLogin: Button
    lateinit var etMobile: EditText
    lateinit var etPassword: EditText
    lateinit var sharedpreferences: SharedPreferences
    lateinit var sharedPreferences_for_profile: SharedPreferences



    var unameList = mutableListOf<String?>("Kushagra")
    var uemailList = mutableListOf<String?>("kushagragoyal1032@gmail.com")
    var umobileList = mutableListOf<String?>("6376719191")
    var upasswordList = mutableListOf<String?>("123")

    val profileInfoList = arrayListOf<UserCredential>()
    override fun onCreate(savedInstanceState: Bundle?)
    {

        super.onCreate(savedInstanceState)
        sharedpreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        sharedPreferences_for_profile = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        val isLoggedIn = sharedpreferences.getBoolean("isLoggedIn", false)
        setContentView(R.layout.activity_login)

        if (isLoggedIn)
        {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


            title = "Login"

            txtRegister = findViewById(R.id.txtRegister)
            txtForgot = findViewById(R.id.txtForgot)
            btnLogin = findViewById(R.id.btnLogin)
            etMobile = findViewById(R.id.etMobile)
            etPassword = findViewById(R.id.etPassword)

             txtRegister.setOnClickListener{

            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        txtForgot.setOnClickListener{

            val intent = Intent(this@LoginActivity, ForgotActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnLogin.setOnClickListener {

            val umobileInp = etMobile.text.toString()
            val upassInp = etPassword.text.toString()

            val queue = Volley.newRequestQueue(this@LoginActivity)
            val url = "http://13.235.250.119/v2/login/fetch_result"

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", umobileInp)
            jsonParams.put("password", upassInp)

            if (ConnectionManager().checkConnectivity(this@LoginActivity))      //check that internet is connected or not
            {
                val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams,
                    Response.Listener
                    {
                        try {
                            val data1 = it.getJSONObject("data")
                            val success = data1.getBoolean("success")
                            if (success) {
                                val credentialJsonObject = data1.getJSONObject("data")
                                val userObject = UserCredential(
                                    credentialJsonObject.getString("user_id"),
                                    credentialJsonObject.getString("name"),
                                    credentialJsonObject.getString("email"),
                                    credentialJsonObject.getString("mobile_number"),
                                    credentialJsonObject.getString("address")
                                )

                                val userid = credentialJsonObject.getString("user_id")
                                val username = credentialJsonObject.getString("name")
                                val email = credentialJsonObject.getString("email")
                                val number = credentialJsonObject.getString("mobile_number")
                                val address = credentialJsonObject.getString("address")


                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                sharedPreferences_profile(userid,username,email,number,address)

                                sharedPreferences()
                                startActivity(intent)
                                finish()

                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Invalid Username Or Password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Some Unexpected Error Occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    Response.ErrorListener
                    {
                        Toast.makeText(
                            this@LoginActivity,
                            "Volly Error Occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {           //jsonObjectRequest open

                    override fun getHeaders(): MutableMap<String, String> {     //send header to API
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "0a603f749180f7"
                        return headers
                    }

                }  //jsonObjectRequest closed
                queue.add(jsonRequest)
            }
            else {
                val dialog = AlertDialog.Builder(this@LoginActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("Open Settings")
                { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }

                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@LoginActivity) // this command is used to close the app
                }
                dialog.create()
                dialog.show()

            }   //else block closed
        }
    }

        fun sharedPreferences()
        {
            sharedpreferences.edit().putBoolean("isLoggedIn", true).apply()
        }

        fun sharedPreferences_profile(userId : String?,username : String?, email : String?, number : String?, address : String?)
        {
            sharedPreferences_for_profile.edit().putString("userid", userId).apply()
            sharedPreferences_for_profile.edit().putString("name1", username).apply()
            sharedPreferences_for_profile.edit().putString("email", email).apply()
            sharedPreferences_for_profile.edit().putString("number", number).apply()
            sharedPreferences_for_profile.edit().putString("address", address).apply()
        }
    }

