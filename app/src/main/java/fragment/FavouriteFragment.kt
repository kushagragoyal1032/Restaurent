package fragment

import adapter.FavouriteRecyclerAdapter
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kushagra.restaurent.R
import database.FoodDatabase
import database.FoodEntity


class FavouriteFragment : Fragment()
 {
        lateinit var recyclerFav : RecyclerView
        lateinit var layoutManager: RecyclerView.LayoutManager
        lateinit var recyclerAdapter: FavouriteRecyclerAdapter
        lateinit var ProgressBar: ProgressBar
        lateinit var ProgressLayout: RelativeLayout
        lateinit var refreshLayout : SwipeRefreshLayout

        var dbFoodList = listOf<FoodEntity>()

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)

        recyclerFav = view.findViewById(R.id.recyclerFav)
        ProgressBar = view.findViewById(R.id.ProgressBar)
        ProgressLayout = view.findViewById(R.id.ProgressLayout)
        refreshLayout = view.findViewById(R.id.refreshLayout)

        ProgressLayout.visibility = View.VISIBLE


        layoutManager = LinearLayoutManager(activity) // it means how we want to print

        dbFoodList = RetrieveFavourites(activity as Context).execute().get()

        if(activity != null)
        {
            ProgressLayout.visibility = View.GONE
            recyclerAdapter = FavouriteRecyclerAdapter(activity as Context, dbFoodList)
            recyclerFav.adapter = recyclerAdapter
            recyclerFav.layoutManager = layoutManager
        }

        refreshLayout.setOnRefreshListener{
                Toast.makeText(context, "Reloding..", Toast.LENGTH_SHORT).show()
                onDestroy()
            refreshLayout.isRefreshing = false
        }
        return view
    }


     class RetrieveFavourites(val context: Context): AsyncTask<Void, Void, List<FoodEntity>>()
     {
         override fun doInBackground(vararg params: Void?): List<FoodEntity>
         {
             val db = Room.databaseBuilder(context, FoodDatabase::class.java, "food-db") .build()

             return db.foodDao().getAllFood()
         }

     }

}