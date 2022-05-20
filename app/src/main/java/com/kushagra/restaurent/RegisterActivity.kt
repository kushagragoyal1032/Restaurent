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


class RegisterActivity : AppCompatActivity()
{

    lateinit var etEmail : EditText
    lateinit var etMobile : EditText
    lateinit var user:EditText
    lateinit var etAddress  : EditText
    lateinit var etPassword : EditText
    lateinit var etPassword2 : EditText
    lateinit var btnRegister : Button
    lateinit var txtSign1 : TextView

    lateinit var sharedpreferences: SharedPreferences
    lateinit var sharedPreferences_for_profile: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        title = "Register Page"

        user = findViewById(R.id.etRName)
        etEmail = findViewById(R.id.etREmail)
        etMobile = findViewById(R.id.etRMobile)
        etAddress = findViewById(R.id.etRAddress)
        etPassword = findViewById(R.id.etRPassword)
        etPassword2 = findViewById(R.id.etRPassword2)
        btnRegister = findViewById(R.id.btnRegister)
        txtSign1 = findViewById(R.id.txtSign1)


        sharedpreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        sharedPreferences_for_profile = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        txtSign1.setOnClickListener{

            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


        btnRegister.setOnClickListener{

            val name = user.text.toString()
            val mobile = etMobile.text.toString()
            val password = etPassword.text.toString()
            val pass2 = etPassword2.text.toString()
            val email = etEmail.text.toString()
            val address = etAddress.text.toString()

            if(name.length < 3 || name.isEmpty())
            {
                Toast.makeText(this@RegisterActivity,"(At least 3 char Required)", Toast.LENGTH_SHORT).show()
            }
            else if(email.isEmpty())
            {
                Toast.makeText(this@RegisterActivity,"(please enter Email)", Toast.LENGTH_SHORT).show()
            }
            else if(mobile.isEmpty() || mobile.length < 10)
            {
                Toast.makeText(this@RegisterActivity,"(10 digits phone number required)", Toast.LENGTH_SHORT).show()
            }
            else if(address.isEmpty())
            {
                Toast.makeText(this@RegisterActivity,"(please enter Address)", Toast.LENGTH_SHORT).show()
            }
            else if(pass2.isEmpty() || pass2.length <4)
            {
                Toast.makeText(this@RegisterActivity,"(At least 4 char Required in Password)",
                    Toast.LENGTH_SHORT).show()
            }
            else if(password.isEmpty() || password.length <4)
            {
                Toast.makeText(this@RegisterActivity,"(At least 4 char Required in confirm Password)",
                    Toast.LENGTH_SHORT).show()
            }
            else if (password != pass2)
            {
                Toast.makeText(this@RegisterActivity,"(Both Password should be same)", Toast.LENGTH_SHORT).show()
            }
            else
            {

                val queue = Volley.newRequestQueue(this@RegisterActivity)
                val url = "http://13.235.250.119/v2/register/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("name", name)
                jsonParams.put("mobile_number", mobile)
                jsonParams.put("password", password)
                jsonParams.put("address", address)
                jsonParams.put("email", email)

                if (ConnectionManager().checkConnectivity(this@RegisterActivity))      //check that internet is connected or not
                {
                    val jsonRequest = object : JsonObjectRequest(
                        Request.Method.POST, url, jsonParams,
                        Response.Listener
                        {
                            try
                            {
                                val data1 = it.getJSONObject("data")
                                val success = data1.getBoolean("success")

                                if (success)
                                {
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

                                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                    Toast.makeText(this@RegisterActivity, "Registered successfully", Toast.LENGTH_SHORT).show()
                                    sharedPreferences_profile(userid,username,email,number,address)
                                    sharedPreferences()
                                    startActivity(intent)
                                    finish()

                                }
                                else
                                {
                                    Toast.makeText(this@RegisterActivity, "Error occured", Toast.LENGTH_SHORT).show()
                                    println("error is $it")
                                }
                            }
                            catch (e: JSONException)
                            {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Some Unexpected Error Occurred!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        Response.ErrorListener
                        {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Volly Error Occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                    {           //jsonObjectRequest open

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
                    val dialog = AlertDialog.Builder(this@RegisterActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings")
                    { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }

                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@RegisterActivity) // this command is used to close the app
                    }
                    dialog.create()
                    dialog.show()
                }   //else block closed
            }
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
    override fun onPause()
    {
        super.onPause()
        finish()
    }
}