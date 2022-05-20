package adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.kushagra.restaurent.ItemListActivity
import com.kushagra.restaurent.R
import com.squareup.picasso.Picasso
import database.FoodDatabase
import database.FoodEntity
import model.Food


class HomeRecyclerAdapter(val context: Context, var itemList: ArrayList<Food>): RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return itemList.size
    }


    override fun onBindViewHolder(holder: HomeViewHolder, position: Int)
    {
        val food = itemList[position]
        holder.FoodName.text = food.FoodName
        holder.FoodPrice.text = "₹ ${food.FoodPrice}/person"
        holder.rating.text = food.FoodRating
        Picasso.get().load(food.FoodImage).error(R.drawable.user).into(holder.FoodImage)


        val foodEntity = FoodEntity(
            food.FoodId?.toInt() as Int,
            food.FoodName.toString(),
            food.FoodPrice.toString(),
            food.FoodRating.toString(),
            food.FoodImage.toString()
        )


        // add to fav. logic

        val checkFav = DBAsyncTask(context, foodEntity  , 1).execute()
        val isFav = checkFav.get()

        if(isFav)
        {
            holder.Heart.setImageResource(R.drawable.ic_baseline_favorite_24)
        }

        else
        {
            holder.Heart.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }

        holder.All_content.setOnClickListener {
            val intent = Intent(context,ItemListActivity::class.java)
            intent.putExtra("Food_Id",food.FoodId)
            intent.putExtra("Rest_Name",food.FoodName)
            Toast.makeText(context, "Clicked on ${holder.FoodName.text}", Toast.LENGTH_SHORT).show()
            context.startActivity(intent)

        }




        holder.Heart.setOnClickListener{
//            Toast.makeText(context, "id of this heart is : ${food.FoodId}", Toast.LENGTH_SHORT).show()

            if(!DBAsyncTask(context, foodEntity, 1).execute().get())
            {
                val async = DBAsyncTask(context, foodEntity, 2).execute()
                val result = async.get()
                if (result)
                {
                    Toast.makeText(context, "Restaurant added to favourites", Toast.LENGTH_SHORT).show()
                    holder.Heart.setImageResource(R.drawable.ic_baseline_favorite_24)
                }
            }
            else
            {
                val async = DBAsyncTask(context, foodEntity, 3).execute()
                val result = async.get()
                if(result)
                {
                    Toast.makeText(context, "Restaurant removed from favourites", Toast.LENGTH_SHORT).show()
                    holder.Heart.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                }
            }
        }
    }

    class HomeViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        val FoodImage: ImageView = view.findViewById(R.id.FoodImage)
        val FoodName: TextView = view.findViewById(R.id.FoodName)
        val FoodPrice: TextView = view.findViewById(R.id.FoodPrice)
        val Heart: ImageView = view.findViewById(R.id.Heart)
        val rating: TextView = view.findViewById(R.id.rating)
        val All_content: LinearLayout = view.findViewById(R.id.All_content)
    }

    // search bar logic

    fun filterList(filteredList: ArrayList<Food>) {//to update the recycler view depending on the search
        itemList = filteredList
        notifyDataSetChanged()
    }

    class DBAsyncTask(val context: Context, val foodEntity: FoodEntity, val mode: Int) : AsyncTask<Void, Void, Boolean>()
    {
        val db = Room.databaseBuilder(context, FoodDatabase::class.java, "food-db").build()

        override fun doInBackground(vararg params: Void?): Boolean
        {
            when(mode)
            {
                1 -> {
                    //check db that food is fav or not
                    val food: FoodEntity = db.foodDao().getFoodByID(foodEntity.food_id.toString())
                    db.close()
                    return food != null
                }

                2 -> {
                    //save the food in database as fav
                    db.foodDao().insertFood(foodEntity)
                    db.close()
                    return true
                }

                3 -> {
                    //delete the book food from database
                    db.foodDao().deleteFood(foodEntity)
                    db.foodDao()
                    return true
                }
            }

            return false
        }

    }


}

