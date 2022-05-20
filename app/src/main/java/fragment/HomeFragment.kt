package fragment

import adapter.HomeRecyclerAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.kushagra.restaurent.R
import model.Food
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import util.ConnectionManager
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.HashMap

class HomeFragment : Fragment() {
    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var ProgressBar: ProgressBar
    lateinit var ProgressLayout: RelativeLayout
    lateinit var etsearch_bar : EditText

    var chack : MenuItem?  = null

    var costComparatorHtoL = Comparator<Food> { food1, food2 ->
        food1.FoodPrice.compareTo(food2.FoodPrice, true)
    }

    var costComparatorLtoH = Comparator<Food> { food1, food2 ->
        food1.FoodPrice.compareTo(food2.FoodPrice, true)
    }

    var rating = Comparator<Food> { food1, food2 ->
        food1.FoodRating.compareTo(food2.FoodRating, true)
    }


    var foodInfoList = arrayListOf<Food>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerHome = view.findViewById(R.id.recyclerHome)
        ProgressBar = view.findViewById(R.id.ProgressBar)
        ProgressLayout = view.findViewById(R.id.ProgressLayout)
        etsearch_bar = view.findViewById(R.id.etsearch_bar)

        ProgressLayout.visibility = View.VISIBLE


        layoutManager = LinearLayoutManager(activity) // it means how we want to print

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val queue = Volley.newRequestQueue(activity as Context)

            val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
                com.android.volley.Response.Listener
                {
                    try {
                        ProgressLayout.visibility = View.GONE
                        val data1 = it.getJSONObject("data")
                        val success = data1.getBoolean("success")
                        if (success) {
                            val data = data1.getJSONArray("data")

                            for (i in 0 until data.length()) {
                                val foodJsonObject = data.getJSONObject(i)
                                val foodObject = Food(
                                    foodJsonObject.getString("id"),
                                    foodJsonObject.getString("name"),
                                    foodJsonObject.getString("rating"),
                                    foodJsonObject.getString("cost_for_one"),
                                    foodJsonObject.getString("image_url")
                                )
                                foodInfoList.add(foodObject)
                                recyclerAdapter = HomeRecyclerAdapter(activity as Context, foodInfoList)
                                recyclerHome.adapter = recyclerAdapter
                                recyclerHome.layoutManager = layoutManager

                            }   // for loop closed
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error Occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } // try closed
                    catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some Unexpected Error Occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },

                com.android.volley.Response.ErrorListener
                {
                    ProgressLayout.visibility = View.GONE
                    Toast.makeText(
                        activity as Context,
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
            queue.add(jsonObjectRequest)
        }   // check internet if block closed

        else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity) // this command is used to close the app
            }
            dialog.create()
            dialog.show()

        }   //else block closed


                                        //searchBar logic


        fun filterFun(strTyped:String){//to filter the recycler view depending on what is typed
            val filteredList= arrayListOf<Food>()

            for (item in foodInfoList)
            {
                if(item.FoodName.toLowerCase().contains(strTyped.toLowerCase()))
                {//to ignore case and if contained add to new list

                    filteredList.add(item)
                }
            }
            recyclerAdapter.filterList(filteredList)
        }
        etsearch_bar.addTextChangedListener(object : TextWatcher{

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(strTyped: Editable?)
            {
                filterFun(strTyped.toString())

            }
        })
        return view

    }





    // sort logic

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
    {
        inflater.inflate(R.menu.sort_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {

        if(chack != null)
        {
            chack?.isChecked = false
        }

        item.isCheckable = true
        item.isChecked = true
        chack = item

    val id = item.itemId

    when(id)
    {
        R.id.SortHtoL ->
        {
            Collections.sort(foodInfoList, costComparatorHtoL)
            foodInfoList.reverse()
        }
        R.id.SortLtoH ->
        {
            Collections.sort(foodInfoList, costComparatorLtoH)
        }
        R.id.Sortrating ->
        {
            Collections.sort(foodInfoList, rating)
            foodInfoList.reverse()
        }

    }

        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }
}
