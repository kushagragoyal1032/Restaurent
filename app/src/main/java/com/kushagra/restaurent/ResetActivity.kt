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

class ResetActivity : AppCompatActivity() {
    lateinit var Otp: EditText
    lateinit var Password: EditText
    lateinit var Password2: EditText
    lateinit var btnNext: Button
    lateinit var sharedpreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)

        Otp = findViewById(R.id.Otp)
        Password = findViewById(R.id.Password)
        Password2 = findViewById(R.id.Password2)
        btnNext = findViewById(R.id.btnNext)

        sharedpreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        btnNext.setOnClickListener {

            val upassword = Password.text.toString()
            val upassword2 = Password2.text.toString()
            val uotp = Otp.text.toString()

            val mobile = sharedpreferences.getString("Mobile", "defaultdata")

            val queue = Volley.newRequestQueue(this@ResetActivity)
            val url = "http://13.235.250.119/v2/reset_password/fetch_result"

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", mobile)
            jsonParams.put("password", upassword)
            jsonParams.put("otp", uotp)

            if (upassword == upassword2)
            {
                if (ConnectionManager().checkConnectivity(this@ResetActivity))      //check that internet is connected or not
                {
                    val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener
                    {
                        try {
                            val data1 = it.getJSONObject("data")
                            val success = data1.getBoolean("success")
                            if (success)
                            {
                                val message = data1.getString("successMessage")

                                val intent = Intent(this@ResetActivity, LoginActivity::class.java)
                                Toast.makeText(this@ResetActivity, message, Toast.LENGTH_SHORT).show()

                                startActivity(intent)
                                finish()

                            }
                            else
                            {
                                Toast.makeText(this@ResetActivity, "error occured", Toast.LENGTH_SHORT).show()
                            }
                        }
                        catch (e: JSONException)
                        {
                            Toast.makeText(
                                this@ResetActivity,
                                "Some Unexpected Error Occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                        Response.ErrorListener
                        {
                            Toast.makeText(
                                this@ResetActivity,
                                "Volly Error Occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {           //jsonObjectRequest open

                        override fun getHeaders(): MutableMap<String, String>
                        {     //send header to API
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
                    val dialog = AlertDialog.Builder(this@ResetActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings")
                    { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }

                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@ResetActivity) // this command is used to close the app
                    }
                    dialog.create()
                    dialog.show()

                }   //else block closed
            }

            else
            {
                Toast.makeText(this@ResetActivity, "both password should be same", Toast.LENGTH_SHORT).show()
            }

        }
    }
}