package adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.kushagra.restaurent.CartActivity
import com.kushagra.restaurent.R
import database.MenuDatabase
import database.MenuEntity
import model.Menu

class ItemListRecyclerAdapter(val context: Context, val itemList: ArrayList<Menu>, val btngotoCart : Button,val RestaurentName : String): RecyclerView.Adapter<ItemListRecyclerAdapter.ItemViewHolder>()
{

    var count = 0
    var itemSelectedId = arrayListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_list_single_row, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int)
    {
        val menu = itemList[position]
        holder.menufoodName.text = menu.MenuName
        holder.menufoodCost.text = "Rs. ${menu.MenuPrice}"
        holder.counting.text = ((position+1).toString())



            //check cart logic

        holder.addButton.setOnClickListener {

                if(holder.addButton.text.toString().equals("add"))
                {
                    count++
                    itemSelectedId.add(menu.MenuId)
                    holder.addButton.text = "Remove"
                    val cartColor = ContextCompat.getColor(context,R.color.cart_color)
                    holder.addButton.setBackgroundColor(cartColor)

                }

                else
                {
                    count--
                    itemSelectedId.remove(menu.MenuId)
                    holder.addButton.text = "add"
                    val cartColor = ContextCompat.getColor(context,R.color.cart_def_color)
                    holder.addButton.setBackgroundColor(cartColor)
                }

            if(count == 0)
            {
               btngotoCart.visibility = View.GONE
            }
            else
            {
                btngotoCart.visibility = View.VISIBLE
            }
        }


        btngotoCart.setOnClickListener{
            val intent = Intent(context,CartActivity::class.java)
            intent.putExtra("Rest_name",RestaurentName)
            intent.putExtra("Rest_id",menu.MenuRest_Id.toString())
            intent.putExtra("All_menu_id",itemSelectedId)
            context.startActivity(intent)
        }
    }

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view)
    {

        val counting: TextView = view.findViewById(R.id.counting)
        val menufoodName : TextView = view.findViewById(R.id.menufoodName)
        val menufoodCost : TextView = view.findViewById(R.id.menufoodCost)
        val addButton : Button = view.findViewById(R.id.addButton)
    }

}