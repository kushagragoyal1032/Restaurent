package adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.kushagra.restaurent.ItemListActivity
import com.kushagra.restaurent.R
import com.squareup.picasso.Picasso
import database.FoodDatabase
import database.FoodEntity

class FavouriteRecyclerAdapter(val context: Context, val itemList: List<FoodEntity>): RecyclerView.Adapter< FavouriteRecyclerAdapter.FavouriteViewHolder>()
{


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_favourite_single_row, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int
    {
    return itemList.size
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int)
    {
        val data = itemList[position]
        holder.FoodName.text = data.foodName
        holder.FoodPrice.text = data.foodPrice
        holder.rating.text = data.foodRating
        Picasso.get().load(data.foodImage).error(R.drawable.user).into(holder.FoodImage)

        holder.All_contentFav.setOnClickListener {
            val intent = Intent(context, ItemListActivity::class.java)
            intent.putExtra("Food_Id",data.food_id.toString())
            intent.putExtra("Rest_Name",data.foodName)
            Toast.makeText(context, "Clicked on ${holder.FoodName.text}", Toast.LENGTH_SHORT).show()
            context.startActivity(intent)

        }

        //fav logic

        val foodEntity = FoodEntity(
            data.food_id?.toInt() as Int,
            data.foodName.toString(),
            data.foodPrice.toString(),
            data.foodRating.toString(),
            data.foodImage.toString()
        )

        holder.Heart.setOnClickListener{
            val async = FavouriteRecyclerAdapter.DBAsyncTask(context, foodEntity).execute()
            val result = async.get()
            if(result)
            {
                Toast.makeText(context, "Restaurant removed from favourites", Toast.LENGTH_SHORT).show()
                holder.Heart.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }
        }


    }

   class FavouriteViewHolder(view: View): RecyclerView.ViewHolder(view)
   {
       val FoodImage: ImageView = view.findViewById(R.id.FoodImage)
       val FoodName: TextView = view.findViewById(R.id.FoodName)
       val FoodPrice: TextView = view.findViewById(R.id.FoodPrice)
       val Heart: ImageView = view.findViewById(R.id.Heart)
       val rating: TextView = view.findViewById(R.id.rating)
       val All_contentFav: LinearLayout = view.findViewById(R.id.All_contentFav)

   }


    class DBAsyncTask(val context: Context, val foodEntity: FoodEntity) : AsyncTask<Void, Void, Boolean>()
    {
        val db = Room.databaseBuilder(context, FoodDatabase::class.java, "food-db").build()

        override fun doInBackground(vararg params: Void?): Boolean
        {

                    //delete the book food from database
                    db.foodDao().deleteFood(foodEntity)
                    db.foodDao()
                    return true
        }
    }
}


