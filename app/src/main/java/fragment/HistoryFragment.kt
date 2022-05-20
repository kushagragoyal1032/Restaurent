package fragment

import adapter.FavouriteRecyclerAdapter
import adapter.HistoryOuterRecyclerAdapter
import adapter.HomeRecyclerAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.kushagra.restaurent.R
import model.Food
import model.HistoryInnerDetails
import model.HistoryOuterDetails
import org.json.JSONException
import util.ConnectionManager


class HistoryFragment : Fragment()
{
    lateinit var  recyclerviewHistory : RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HistoryOuterRecyclerAdapter
//    lateinit var recyclerAdapter2: HistoryInnerrRecyclerAdapter
    lateinit var ProgressBar: ProgressBar
    lateinit var ProgressLayout: RelativeLayout
    lateinit var sharedPreferences_for_profile: SharedPreferences

    var orderOuterInfoList = arrayListOf<HistoryOuterDetails>()
    var userId : String? = "100"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        recyclerviewHistory = view.findViewById(R.id.recyclerviewHistory)
        ProgressBar = view.findViewById(R.id.ProgressBar)
        ProgressLayout = view.findViewById(R.id.ProgressLayout)
        ProgressLayout.visibility = View.VISIBLE

        layoutManager = LinearLayoutManager(activity) // it means how we want to print

        sharedPreferences_for_profile = requireContext().getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        userId = sharedPreferences_for_profile.getString("userid", "defaultdata")

        if (ConnectionManager().checkConnectivity(activity as Context))
        {
            val queue = Volley.newRequestQueue(activity as Context)

            val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"

            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET, url, null,
                com.android.volley.Response.Listener
                {
                    try {
                        ProgressLayout.visibility = View.GONE
                        val data1 = it.getJSONObject("data")
                        val success = data1.getBoolean("success")
                        if (success) {
                            val data = data1.getJSONArray("data")

                            for (i in 0 until data.length())
                            {
                                val orderJsonObject = data.getJSONObject(i)
                                val orderObject = HistoryOuterDetails(
                                    orderJsonObject.getString("order_id"),
                                    orderJsonObject.getString("restaurant_name"),
                                    orderJsonObject.getString("total_cost"),
                                    orderJsonObject.getString("order_placed_at")
                                )

                              /*  val food_details = orderJsonObject.getJSONArray("food_items")
                                for(j in 0 until food_details.length())
                                {
                                    val foodJsonObject = food_details.getJSONObject(j)
                                    val orderObject2 = HistoryInnerDetails(
                                        foodJsonObject.getString("food_item_id"),
                                        foodJsonObject.getString("name"),
                                        foodJsonObject.getString("cost")
                                    )

                                    orderInnerInfoList.add(orderObject2)
                                    recyclerAdapter = HistoryOuterRecyclerAdapter(activity as Context, orderInnerInfoList)
                                    recyclerviewHistory.adapter = recyclerAdapter2
                                    recyclerviewHistory.layoutManager = recyclerAdapter2
                                }*/

                                orderOuterInfoList.add(orderObject)
                                recyclerAdapter = HistoryOuterRecyclerAdapter(activity as Context, orderOuterInfoList,userId)
                                recyclerviewHistory.adapter = recyclerAdapter
                                recyclerviewHistory.layoutManager = layoutManager



                            }   // for loop closed
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error Occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } // try closed
                    catch (e: JSONException)
                    {
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
            queue.add(jsonObjectRequest)
        }   // check internet if block closed

        else
        {
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



        return view
    }


}