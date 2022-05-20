package com.kushagra.restaurent

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import model.UserCredential
import org.json.JSONException
import org.json.JSONObject
import util.ConnectionManager

class ForgotActivity : AppCompatActivity()
{
    lateinit var txtBackLogin: TextView
    lateinit var etFMobile : EditText
    lateinit var etEmail : EditText
    lateinit var btnNext : Button
    lateinit var txtPassHaving : TextView
    lateinit var sharedpreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)

        title = "Forgot Password"

        txtBackLogin = findViewById(R.id.txtBackLogin)
        etFMobile = findViewById(R.id.etFMobile)
        etEmail = findViewById(R.id.etEmail)
        btnNext = findViewById(R.id.btnNext)
        txtPassHaving = findViewById(R.id.txtPassHaving)

        sharedpreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        txtBackLogin.setOnClickListener{

            val intent = Intent(this@ForgotActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        txtPassHaving.setOnClickListener {
            val intent = Intent(this@ForgotActivity, ResetActivity::class.java)
            startActivity(intent)
        }


        btnNext.setOnClickListener {

            /*if (mobile.length < 10)
            {
                Toast.makeText(this@ForgotActivity, "(10 digits phone number required)", Toast.LENGTH_SHORT).show()
            }*/
            /*if (email.isEmpty()) {
                Toast.makeText(this@ForgotActivity, "(please enter Email)", Toast.LENGTH_SHORT)
                    .show()
            }
            else {*/

            val umobile = etFMobile.text.toString()
            val uemail = etEmail.text.toString()

            sharedPreferences(umobile)
            val queue = Volley.newRequestQueue(this@ForgotActivity)
                val url = " http://13.235.250.119/v2/forgot_password/fetch_result"

                val jsonParams = JSONObject()

                jsonParams.put("mobile_number", umobile)
                jsonParams.put("email", uemail)

                if (ConnectionManager().checkConnectivity(this@ForgotActivity))      //check that internet is connected or not
                {
                    val jsonRequest = object : JsonObjectRequest(
                        Request.Method.POST, url, jsonParams,
                        Response.Listener
                        {
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success)
                                {
                                    val firstTry = data.getBoolean("first_try")
                                    if (firstTry== true)
                                    {
                                        val intent = Intent(this@ForgotActivity, ResetActivity::class.java)
                                        Toast.makeText(this@ForgotActivity, "OTP Sent successfully", Toast.LENGTH_SHORT).show()
                                        startActivity(intent)
                                        finish()
                                    }
                                    else
                                    {
                                        Toast.makeText(this@ForgotActivity, "OTP sent, check email valid for 24 Hours", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                else {
                                    Toast.makeText(
                                        this@ForgotActivity,
                                        "Error occured",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    println("error is $it")
                                }
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@ForgotActivity,
                                    "Some Unexpected Error Occurred!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },

                        Response.ErrorListener
                        {
                            Toast.makeText(
                                this@ForgotActivity,
                                "Volly Error Occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                    {           //jsonObjectRequest open

                        override fun getHeaders(): MutableMap<String, String> {     //send header to API
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "0a603f749180f7"
                            return headers
                        }

                    }  //jsonObjectRequest closed
                    queue.add(jsonRequest)
                }

                else
                {
                    val dialog = AlertDialog.Builder(this@ForgotActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings")
                    { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }

                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@ForgotActivity) // this command is used to close the app
                    }
                    dialog.create()
                    dialog.show()

                }   //else block closed
        }

    }
    fun sharedPreferences(Mobile : String?)
    {
        sharedpreferences.edit().putString("Mobile", Mobile).apply()

    }

}