package com.example.lefto.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lefto.R
import com.example.lefto.view.RestaurantActivity
import com.example.lefto.holder.LeftoverViewHolder
import com.example.lefto.model.LeftOverItem
//import kotlinx.android.synthetic.main.list_item_layout.view.*
import kotlinx.android.synthetic.main.list_leftover_layout.view.*
import kotlinx.android.synthetic.main.list_leftover_layout.view.tv_name
import kotlinx.android.synthetic.main.list_leftover_layout.view.tv_quantity
import kotlinx.android.synthetic.main.list_see_leftover_layout.view.*

class SeeLeftoverAdapter (val leftovers: List<LeftOverItem>) :
    RecyclerView.Adapter<LeftoverViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeftoverViewHolder {
        val view = LayoutInflater.from(parent.context).
            inflate(R.layout.list_see_leftover_layout, parent,false)
        val viewHolder = LeftoverViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: LeftoverViewHolder, position: Int) {
        val leftover = leftovers[position]
        holder.item.tv_name.text = leftover.name
        holder.item.tv_description.text = leftover.description
        holder.item.tv_quantity.text = leftover.quantity.toString()
        if (leftover.halal) {
            holder.item.img_halal.visibility = View.VISIBLE
        }
        if (leftover.vegan) {
            holder.item.img_vegan.visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        return leftovers.size
    }


}