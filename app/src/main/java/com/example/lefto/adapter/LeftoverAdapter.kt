package com.example.lefto.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lefto.R
import com.example.lefto.view.RestaurantActivity
import com.example.lefto.holder.LeftoverViewHolder
import com.example.lefto.model.LeftOverItem
//import kotlinx.android.synthetic.main.list_item_layout.view.*
import kotlinx.android.synthetic.main.list_leftover_layout.view.*

class LeftoverAdapter (val leftovers: List<LeftOverItem>) :
    RecyclerView.Adapter<LeftoverViewHolder>() {
    private val DAO = RestaurantActivity.DAO
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeftoverViewHolder {
        val view = LayoutInflater.from(parent.context).
            inflate(R.layout.list_leftover_layout, parent,false)
        val viewHolder = LeftoverViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: LeftoverViewHolder, position: Int) {
        val leftover = leftovers[position]
        holder.item.tv_name.text = leftover.name
        holder.item.tv_quantity.text = leftover.quantity.toString()
        holder.item.btn_plus.setOnClickListener {
            leftover.quantity++
            holder.item.tv_quantity.text = leftover.quantity.toString()

            // Add 1 to related leftover in DB TODO
            DAO.updateLeftoverQuantity(leftover)
        }

        holder.item.btn_minus.setOnClickListener {
            var newQty = leftover.quantity - 1

            if (newQty >= 0) {
                // Sub 1 to related leftover in DB TODO
                leftover.quantity--
                holder.item.tv_quantity.text =
                    newQty.toString()
                DAO.updateLeftoverQuantity(leftover)
            } // if click on qty = 0, do nothing
        }
    }

    override fun getItemCount(): Int {
        return leftovers.size
    }


}