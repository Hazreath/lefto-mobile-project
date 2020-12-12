package com.example.lefto.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lefto.R
import com.example.lefto.holder.LeftoverViewHolder
import com.example.lefto.model.LeftOverItem
//import kotlinx.android.synthetic.main.list_item_layout.view.*
import kotlinx.android.synthetic.main.list_leftover_layout.view.*
import java.lang.Integer.parseInt

class LeftoverAdapter (val leftovers: List<LeftOverItem>) :
    RecyclerView.Adapter<LeftoverViewHolder>() {

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
            holder.item.tv_quantity.text =
                (parseInt(holder.item.tv_quantity.text.toString()) + 1).toString()

            // Add 1 to related leftover in DB TODO
        }

        holder.item.btn_minus.setOnClickListener {
            var newQty = parseInt(holder.item.tv_quantity.text.toString()) - 1

            if (newQty > 0) {
                // Sub 1 to related leftover in DB TODO
                holder.item.tv_quantity.text =
                    newQty.toString()
            } else {
                // Delete leftover entry TODO

            }
        }
    }

    override fun getItemCount(): Int {
        return leftovers.size
    }


}