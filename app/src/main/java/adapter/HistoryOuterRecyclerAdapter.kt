package adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.kushagra.restaurent.R
import model.Cart
import model.HistoryInnerDetails
import model.HistoryOuterDetails
import org.json.JSONException
import util.ConnectionManager

class HistoryOuterRecyclerAdapter(val context: Context, val itemList: ArrayList<HistoryOuterDetails>, val userId : String?):RecyclerView.Adapter<HistoryOuterRecyclerAdapter.HistoryOuterHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryOuterHolder
    {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_outer_history_single_row, parent, false)
        return HistoryOuterHolder(view)
    }

    override fun getItemCount(): Int
    {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HistoryOuterHolder, position: Int)
    {
        val HistoryOuter = itemList[position]
        holder.restName.text =HistoryOuter.RestaurantName
        holder.history_order_date.text = HistoryOuter.TimeAndDate.replace("-","/").take(8)

        val orderPerInfoList = arrayListOf<Cart>()

        var layoutManager = LinearLayoutManager(context)
        var recyclerAdapter : CartRecyclerAdapter

        if (ConnectionManager().checkConnectivity(context))
        {
            val queue = Volley.newRequestQueue(context)

            val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"

            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET, url, null,
                com.android.volley.Response.Listener
                {
                    try {
                        val data1 = it.getJSONObject("data")
                        val success = data1.getBoolean("success")
                        if (success)
                        {
                            val data = data1.getJSONArray("data")
                            val orderJsonObject = data.getJSONObject(position)
                            val food_details = orderJsonObject.getJSONArray("food_items")
                            for(j in 0 until food_details.length())
                            {
                                val foodJsonObject = food_details.getJSONObject(j)
                                val orderObject2 = Cart(
                                    foodJsonObject.getString("name"),
                                    foodJsonObject.getString("cost")
                                )

                                orderPerInfoList.add(orderObject2)
                                recyclerAdapter = CartRecyclerAdapter(context, orderPerInfoList)
                                holder.recyclerInRecyclerHistory.adapter = recyclerAdapter
                                holder.recyclerInRecyclerHistory.layoutManager = layoutManager
                            }




                        } else {
                            Toast.makeText(
                                context,
                                "Some Error Occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } // try closed
                    catch (e: JSONException)
                    {
                        Toast.makeText(
                            context,
                            "Some Unexpected Error Occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },

                com.android.volley.Response.ErrorListener
                {
                    Toast.makeText(context, "Volly Error Occurred!!!", Toast.LENGTH_SHORT).show()

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

    }

    class HistoryOuterHolder(view: View): RecyclerView.ViewHolder(view)
    {
        val restName : TextView = view.findViewById(R.id.restName)
        val history_order_date : TextView  = view.findViewById(R.id.history_order_date)
        val recyclerInRecyclerHistory : RecyclerView = view.findViewById(R.id.recyclerInRecyclerHistory)
    }
}