package com.kushagra.restaurent

import adapter.CartRecyclerAdapter
import adapter.ItemListRecyclerAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import model.Cart
import model.Menu
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity()
{
    lateinit var ToolBar : androidx.appcompat.widget.Toolbar
    lateinit var recyclercart : RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var btnplaceOrder : Button
    lateinit var recyclerAdapter : CartRecyclerAdapter
    lateinit var restname : TextView
    lateinit var sharedPreferences_for_profile: SharedPreferences

    var RestaurentName: String? = "100"
    var RestaurentId: String? = "100"
    var userId : String? = "100"
    var TotalPrice = 0

    val cartInfoList = arrayListOf<Cart>()
    lateinit var AllmenuId : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        ToolBar = findViewById(R.id.ToolBar)
        recyclercart = findViewById(R.id.recyclercart)
        btnplaceOrder = findViewById(R.id.btnplaceOrder)
        layoutManager = LinearLayoutManager(this)       // this line means we want to display items in linear layout
        restname = findViewById(R.id.restname)

        if(intent != null)
        {
            RestaurentName = intent.getStringExtra("Rest_name")
            RestaurentId = intent.getStringExtra("Rest_id")
            AllmenuId = intent.getStringArrayListExtra("All_menu_id") as ArrayList<String>

        }
        else
        {
            Toast.makeText(this@CartActivity, "Some error is occured", Toast.LENGTH_SHORT).show()
            finish()
        }

        setSupportActionBar(ToolBar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        ToolBar.setNavigationOnClickListener(View.OnClickListener {
            onBackPressed()
            finish()
        })

        restname.text = RestaurentName
        sharedPreferences_for_profile = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        userId = sharedPreferences_for_profile.getString("userid", "defaultdata")

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/${RestaurentId?.toInt()}"

        val jsonRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener
            {
                try {

                    val Data = it.getJSONObject("data")
                    val success = Data.getBoolean("success")
                    if (success)
                    {
                        val data = Data.getJSONArray("data")
                        for (i in 0 until data.length())
                        {

                            val cartJsonObject = data.getJSONObject(i)

                            if(AllmenuId.contains(cartJsonObject.getString("id")))
                            {
                                val menuObject = Cart(
                                    cartJsonObject.getString("name"),
                                    cartJsonObject.getString("cost_for_one")
                                )

                                TotalPrice += cartJsonObject.getString("cost_for_one").toInt()      //calc. total price

                                cartInfoList.add(menuObject)
                            }
                            recyclerAdapter = CartRecyclerAdapter(this, cartInfoList)
                            recyclercart.adapter = recyclerAdapter
                            recyclercart.layoutManager = layoutManager
                        }   // for loop closed
                    }
                    btnplaceOrder.text = "Place Order (Total Rs. ${TotalPrice})"
                }
                catch (e: Exception)
                {
                    Toast.makeText(this@CartActivity, "Some Unexpected Error Occurred!!!", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener
            {
                Toast.makeText(this@CartActivity, "Volley Error Occurred!!!", Toast.LENGTH_SHORT).show()

            })

        {   // jsonRequest open
            override fun getHeaders(): MutableMap<String, String> {     //send header to API
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["token"] = "0a603f749180f7"
                return headers
            }
        } // jsonRequest Closed
        queue.add(jsonRequest)

//------------------------------------------------------------------------------------------------------
        btnplaceOrder.setOnClickListener {
            val foodJsonArray = JSONArray()
            for(i in AllmenuId)
            {
                val foodObject = JSONObject()
                foodObject.put("food_item_id",i)
                foodJsonArray.put(foodObject)
            }

            val queue1 = Volley.newRequestQueue(this)
            val url = " http://13.235.250.119/v2/place_order/fetch_result/"

            val jsonParams = JSONObject()
            jsonParams.put("user_id", userId)
            jsonParams.put("restaurant_id", RestaurentId)
            jsonParams.put("total_cost", TotalPrice)
            jsonParams.put("food", foodJsonArray)

            val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams,
                Response.Listener
                {
                    try {

                        val Data = it.getJSONObject("data")
                        val success = Data.getBoolean("success")
                        if (success)
                        {
                            Toast.makeText(this@CartActivity, "Your Order Placed Successfully!!!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@CartActivity, PlaceOderderActivity::class.java)
                                startActivity(intent)
                            finish()
                        }
                        else
                        {
                            Toast.makeText(this@CartActivity, "Some Error Occurred!!!", Toast.LENGTH_SHORT).show()
                        }

                    }
                    catch (e: Exception)
                    {
                        Toast.makeText(this@CartActivity, "Some Unexpected Error Occurred!!!", Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener
                {
                    Toast.makeText(this@CartActivity, "Volley Error Occurred!!!", Toast.LENGTH_SHORT).show()

                })

            {   // jsonRequest open
                override fun getHeaders(): MutableMap<String, String> {     //send header to API
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    headers["token"] = "0a603f749180f7"
                    return headers
                }
            } // jsonRequest Closed
            queue1.add(jsonRequest)

        }

    }
    override fun onBackPressed()
    {

        super.onBackPressed()
    }
}
