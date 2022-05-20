package adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kushagra.restaurent.R
import model.Cart

class CartRecyclerAdapter(val context: Context, val itemList: ArrayList<Cart>): RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>()
{


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder
    {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_cart_single_row, parent,false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return itemList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int)
    {
        val cart = itemList[position]
        holder.cartfoodName.text = cart.CartMenuName
        holder.cartfoodCost.text = "Rs. ${cart.CartMenuPrice}/-"
    }
    class CartViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        val cartfoodName: TextView = view.findViewById(R.id.cartfoodName)
        val cartfoodCost: TextView = view.findViewById(R.id.cartfoodCost)

    }



}
