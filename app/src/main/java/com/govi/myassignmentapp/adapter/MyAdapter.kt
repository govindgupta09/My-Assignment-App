package com.govi.myassignmentapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.govi.myassignmentapp.R
import com.govi.myassignmentapp.activities.DescriptionActivity
import com.govi.myassignmentapp.models.Product
import de.hdodenhof.circleimageview.CircleImageView
import java.util.ArrayList

class MyAdapter(private var context: Context, private var myData: ArrayList<Product>, private val onItemClick: (Product) -> Unit):
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)  {
        val thumbnail: CircleImageView = itemView.findViewById(R.id.iv_thumbnail)
        val title: TextView = itemView.findViewById(R.id.tv_title)
        val brand: TextView = itemView.findViewById(R.id.tv_brand)
        val category: TextView = itemView.findViewById(R.id.tv_category)
        val ratings: TextView = itemView.findViewById(R.id.iv_ratings)
        val discountPercentage: TextView = itemView.findViewById(R.id.tv_discountPercentage)
        val price: TextView = itemView.findViewById(R.id.tv_price)
        val stock: TextView = itemView.findViewById(R.id.tv_stock)
        val description: TextView = itemView.findViewById(R.id.tv_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_recycler_view,parent,false)
        return MyViewHolder(itemView)
    }

    /**
     * Here, gets the number of items in the list.
     */
    override fun getItemCount(): Int {
        return myData.size
    }

    /**
     * Here binds each item in the ArrayList to a view.
     * This is called when RecyclerView needs a new {ViewHolder} of the given type to represent an item.
     * This new ViewHolder should be constructed with a new View that can represent the items of the
     * given type. We can either create a new View manually or inflate it from an XML layout file.
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = myData[position]

        holder.title.text = currentItem.title
        holder.brand.text = currentItem.brand
        holder.category.text = currentItem.category

        holder.ratings.text = currentItem.rating.toString()+" ratings"
        holder.ratings.setTextColor(ContextCompat.getColor(context, R.color.ratingTextColor))

        holder.discountPercentage.text = currentItem.discountPercentage.toString()+"% off"
        holder.price.text = "$"+currentItem.price.toString()+" USD"

        holder.stock.text = "Only "+currentItem.stock.toString()+" items available"
        holder.stock.setTextColor(ContextCompat.getColor(context, R.color.stockTextColor))

        holder.description.text = currentItem.description

        Glide.with(context).load(currentItem.thumbnail).into(holder.thumbnail);

        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
            val intent = Intent(context, DescriptionActivity::class.java)
            intent.putExtra("Product",currentItem)
            context.startActivity(intent)
        }
    }

}