package com.kushagra.restaurent

import adapter.ItemListRecyclerAdapter
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import database.FoodEntity
import model.Food
import model.Menu


class ItemListActivity : AppCompatActivity()
{
    lateinit var ToolBar : androidx.appcompat.widget.Toolbar
    lateinit var Heart : ImageView
    lateinit var recyclerviewList : RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter : ItemListRecyclerAdapter
    lateinit var ProgressLayout : RelativeLayout
    lateinit var ProgressBar : ProgressBar
    lateinit var btntogoCart : Button

    val menuInfoList = arrayListOf<Menu>()
    var FoodId : String? = "100"
    var RestaurentName : String? = "200"
    var Restname : String = "3"

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        ToolBar = findViewById(R.id.ToolBar)
        Heart = findViewById(R.id.Heart)
        recyclerviewList = findViewById(R.id.recyclerviewList)
        ProgressBar = findViewById(R.id.ProgressBar)
        ProgressLayout = findViewById(R.id.ProgressLayout)
        btntogoCart = findViewById(R.id.btntogoCart)
        ProgressLayout.visibility = View.VISIBLE
        ProgressBar.visibility = View.VISIBLE
        layoutManager = LinearLayoutManager(this)       // this line means we want to display items in linear layout

        btntogoCart.visibility = View.GONE  //hide the button for the fist time





        if(intent != null)
        {
            FoodId = intent.getStringExtra("Food_Id")
            RestaurentName = intent.getStringExtra("Rest_Name")
            Restname = RestaurentName.toString()
        }
        else
        {
            Toast.makeText(this@ItemListActivity, "Some error is occured", Toast.LENGTH_SHORT).show()
            finish()
        }
        if(FoodId == "100")
        {
            Toast.makeText(this@ItemListActivity, "Some Unexpected error is occured", Toast.LENGTH_SHORT).show()
            finish()
        }

        setSupportActionBar(ToolBar)
        supportActionBar?.title = RestaurentName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        ToolBar.setNavigationOnClickListener(View.OnClickListener {
            onBackPressed()
            finish()
        })

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$FoodId"

            val jsonRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener
                {
                    try {
                        ProgressLayout.visibility = View.GONE
                        val Data = it.getJSONObject("data")
                        val success = Data.getBoolean("success")
                        if (success)
                        {
                            val data = Data.getJSONArray("data")
                            for (i in 0 until data.length())
                            {
                                val menuJsonObject = data.getJSONObject(i)
                                val menuObject = Menu(
                                    menuJsonObject.getString("id"),
                                    menuJsonObject.getString("name"),
                                    menuJsonObject.getString("cost_for_one"),
                                    menuJsonObject.getString("restaurant_id")
                                )
                                menuInfoList.add(menuObject)
                                recyclerAdapter = ItemListRecyclerAdapter(this, menuInfoList, btntogoCart,Restname)
                                recyclerviewList.adapter = recyclerAdapter
                                recyclerviewList.layoutManager = layoutManager
                            }   // for loop closed
                        }
                    }
                    catch (e: Exception)
                    {
                        Toast.makeText(this@ItemListActivity, "Some Unexpected Error Occurred!!!", Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener
                {
                    Toast.makeText(this@ItemListActivity, "Volley Error Occurred!!!", Toast.LENGTH_SHORT).show()

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
        }

    override fun onBackPressed()
    {

        super.onBackPressed()
    }

    }
