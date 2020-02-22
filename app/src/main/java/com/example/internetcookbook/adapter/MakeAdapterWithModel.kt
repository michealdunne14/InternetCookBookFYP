package com.example.internetcookbook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.internetcookbook.R
import com.example.internetcookbook.models.PostMethodOidModel
import kotlinx.android.synthetic.main.make_items.view.*

class MakeAdapterWithModel(private var makeArrayList: MutableList<PostMethodOidModel>) : RecyclerView.Adapter<MakeAdapterWithModel.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.make_items,
                parent,
                false
            )
        )
    }

    //  Item Count
    override fun getItemCount(): Int = makeArrayList.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val postModel = makeArrayList[holder.adapterPosition]
        holder.bind(postModel.methodStep)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(value: String) {
            itemView.mMakeMethod.text = value
        }
    }

}